package cn.fandmc.flametech.multiblock.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.multiblock.base.BlockOffset;
import cn.fandmc.flametech.multiblock.base.MultiblockStructure;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 治炼炉多方块结构 - 修复版本
 * 结构：
 * 0 h 0  (Y+0) - 主方块层：下界砖栅栏（主方块）
 * 0 t 0  (Y-1) - 发射器
 * 0 f 0  (Y-2) - 火
 * <p>
 * h = 下界砖栅栏 (主方块)
 * t = 发射器
 * f = 火
 */
public class SmeltingFurnace extends MultiblockStructure {

    // 存储每个结构当前会话的使用次数（只在当前运行时有效）
    private static final Map<String, Integer> currentSessionUsage = new ConcurrentHashMap<>();
    private static final int MAX_USAGE_BEFORE_FIRE_OUT = 5;

    public SmeltingFurnace(Main plugin) {
        super(plugin, "smelting_furnace",
                plugin.getConfigManager().getLang("multiblock.smelting_furnace.name"),
                createStructure(),
                5);
    }

    private static Map<BlockOffset, Material> createStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();

        // Y+0 层：下界砖栅栏（主方块）
        structure.put(new BlockOffset(0, 0, 0), Material.NETHER_BRICK_FENCE);

        // Y-1 层：发射器
        structure.put(new BlockOffset(0, -1, 0), Material.DISPENSER);

        // Y-2 层：火
        structure.put(new BlockOffset(0, -2, 0), Material.FIRE);

        return structure;
    }

    @Override
    public void onActivate(Player player, Location location, PlayerInteractEvent event) {
        if (!canUse(player)) {
            MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_NOT_UNLOCKED,
                    "%machine%", getDisplayName());
            return;
        }

        try {
            // 获取结构方块 - 注意：location现在是主方块（下界砖栅栏）位置
            Block dispenserBlock = location.clone().add(0, -1, 0).getBlock();
            Block fireBlock = location.clone().add(0, -2, 0).getBlock();

            if (dispenserBlock.getType() != Material.DISPENSER) {
                MessageUtils.sendLocalizedMessage(player, "multiblock.smelting_furnace.error");
                return;
            }

            // 检查火是否还在 - 这是最重要的检查
            if (fireBlock.getType() != Material.FIRE) {
                MessageUtils.sendLocalizedMessage(player, "multiblock.smelting_furnace.fire_out");
                MessageUtils.sendLocalizedMessage(player, "multiblock.smelting_furnace.relight_hint");
                return;
            }

            Dispenser dispenser = (Dispenser) dispenserBlock.getState();
            Inventory dispenserInv = dispenser.getInventory();

            if (isEmpty(dispenserInv)) {
                sendWelcomeMessages(player);
                // 重要：为新建结构重置使用次数
                resetUsageCount(location);
            } else {
                attemptSmelting(player, dispenserInv, location);
            }

        } catch (Exception e) {
            MessageUtils.logError("Error in smelting furnace activation: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c处理治炼炉时发生错误");
        }
    }

    private boolean isEmpty(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    private void sendWelcomeMessages(Player player) {
        MessageUtils.sendLocalizedMessage(player, "multiblock.smelting_furnace.created");
        MessageUtils.sendLocalizedMessage(player, "multiblock.smelting_furnace.hint");
    }

    private void attemptSmelting(Player player, Inventory dispenserInv, Location location) {
        String locationKey = getLocationKey(location);

        // 获取当前会话的使用次数
        int currentUsage = currentSessionUsage.getOrDefault(locationKey, 0);
        boolean smeltingSuccess = performSmelting(player, dispenserInv, location);

        if (smeltingSuccess) {
            currentSessionUsage.put(locationKey, currentUsage + 1);
        }

        if (currentUsage >= MAX_USAGE_BEFORE_FIRE_OUT) {
            extinguishFire(location);
            currentSessionUsage.remove(locationKey);
            MessageUtils.sendLocalizedMessage(player, "multiblock.smelting_furnace.final_use_warning");
            return;
        }
    }

    private boolean performSmelting(Player player, Inventory dispenserInv, Location location) {
        try {
            // 获取输入物品
            Map<Integer, ItemStack> inputs = getInputsFromDispenser(dispenserInv);

            // 查找匹配的配方
            Optional<Recipe> recipeOpt = plugin.getRecipeManager()
                    .findMatchingRecipe(getStructureId(), inputs);

            if (recipeOpt.isEmpty()) {
                MessageUtils.sendLocalizedMessage(player, "multiblock.smelting_furnace.no_recipe");
                return false;
            }

            Recipe recipe = recipeOpt.get();

            // 检查配方解锁状态
            String recipeUnlockId = "recipe." + recipe.getRecipeId();
            if (!plugin.getUnlockManager().isUnlocked(player, recipeUnlockId)) {
                MessageUtils.sendLocalizedMessage(player, Messages.RECIPE_NOT_UNLOCKED,
                        "%recipe%", recipe.getDisplayName());
                return false;
            }

            // 播放熔炼效果
            playSmeltingEffects(location);

            // 消耗材料
            if (!consumeIngredients(dispenserInv, recipe)) {
                return false;
            }

            // 添加结果物品
            ItemStack result = recipe.getResult();
            Map<Integer, ItemStack> leftover = dispenserInv.addItem(result.clone());

            // 处理溢出
            if (!leftover.isEmpty()) {
                Location dropLocation = location.clone().add(0.5, 1.5, 0.5);
                for (ItemStack overflow : leftover.values()) {
                    if (dropLocation.getWorld() != null) {
                        dropLocation.getWorld().dropItemNaturally(dropLocation, overflow);
                    }
                }
            }

            MessageUtils.sendLocalizedMessage(player, "multiblock.smelting_furnace.smelting_success",
                    "%item%", recipe.getDisplayName());
            return true;

        } catch (Exception e) {
            MessageUtils.logError("Error during smelting process: " + e.getMessage());
            return false;
        }
    }

    // 添加辅助方法
    private Map<Integer, ItemStack> getInputsFromDispenser(Inventory dispenserInv) {
        Map<Integer, ItemStack> inputs = new HashMap<>();

        for (int i = 0; i < Math.min(9, dispenserInv.getSize()); i++) {
            ItemStack item = dispenserInv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                inputs.put(i, item.clone());
            }
        }

        return inputs;
    }

    private boolean consumeIngredients(Inventory dispenserInv, Recipe recipe) {
        Map<Integer, ItemStack> required = recipe.getIngredients();

        // 验证材料
        for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
            int slot = entry.getKey();
            ItemStack requiredItem = entry.getValue();

            if (slot >= dispenserInv.getSize()) {
                return false;
            }

            ItemStack current = dispenserInv.getItem(slot);
            if (current == null || current.getType() != requiredItem.getType() ||
                    current.getAmount() < requiredItem.getAmount()) {
                return false;
            }
        }

        // 消耗材料
        for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
            int slot = entry.getKey();
            ItemStack requiredItem = entry.getValue();

            ItemStack current = dispenserInv.getItem(slot);
            if (current != null) {
                current.setAmount(current.getAmount() - requiredItem.getAmount());
                if (current.getAmount() <= 0) {
                    dispenserInv.setItem(slot, null);
                }
            }
        }

        return true;
    }

    private void extinguishFire(Location location) {
        Block fireBlock = location.clone().add(0, -2, 0).getBlock();
        if (fireBlock.getType() == Material.FIRE) {
            fireBlock.setType(Material.AIR);

            // 播放火焰熄灭效果
            if (location.getWorld() != null) {
                location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f);
                location.getWorld().spawnParticle(Particle.SMOKE,
                        location.clone().add(0.5, -1.5, 0.5), 10, 0.3, 0.3, 0.3, 0.1);
            }
        }
    }

    private void playSmeltingEffects(Location location) {
        if (location.getWorld() != null) {
            location.getWorld().playSound(location, Sound.BLOCK_FURNACE_FIRE_CRACKLE, 1.0f, 1.0f);
            location.getWorld().spawnParticle(Particle.FLAME,
                    location.clone().add(0.5, -1.5, 0.5), 8, 0.2, 0.2, 0.2, 0.02);
        }
    }

    /**
     * 重置使用次数（当重新点火或新建结构时调用）
     */
    private void resetUsageCount(Location location) {
        String locationKey = getLocationKey(location);
        currentSessionUsage.put(locationKey, 0);
        MessageUtils.logDebug("重置治炼炉使用次数: " + locationKey);
    }

    private String getLocationKey(Location location) {
        return location.getWorld().getName() + "_" +
                location.getBlockX() + "_" +
                location.getBlockY() + "_" +
                location.getBlockZ();
    }

    /**
     * 获取当前使用次数（用于调试）
     */
    public static int getCurrentUsage(Location location) {
        String locationKey = location.getWorld().getName() + "_" +
                location.getBlockX() + "_" +
                location.getBlockY() + "_" +
                location.getBlockZ();
        return currentSessionUsage.getOrDefault(locationKey, 0);
    }

    /**
     * 清理所有使用记录（可在插件重载时调用）
     */
    public static void clearAllUsageRecords() {
        currentSessionUsage.clear();
        MessageUtils.logDebug("清理所有治炼炉使用记录");
    }

    @Override
    public boolean canCraft(String recipeId) {
        // TODO: 这里可以添加治炼炉专用配方的检查
        return true;
    }
}