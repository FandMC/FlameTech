package cn.fandmc.flametech.multiblock.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.multiblock.base.BlockOffset;
import cn.fandmc.flametech.multiblock.base.MultiblockStructure;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.utils.ItemUtils;
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

/**
 * 压力机多方块结构 - 支持X和Y两个方向的3x3竖直平面
 *
 * X方向结构布局（从前方看）：
 * Y+1: p h p  (平滑石头 活塞(朝下) 平滑石头)
 * Y+0: h g h  (活塞(朝右) 玻璃(主方块) 活塞(朝左))
 * Y-1: p t p  (平滑石头 发射器 平滑石头)
 *
 * Y方向结构布局（从前方看）：
 * Y+1: p h p  (平滑石头 活塞(朝下) 平滑石头)
 * Y+0: h g h  (活塞(朝前) 玻璃(主方块) 活塞(朝后))
 * Y-1: p t p  (平滑石头 发射器 平滑石头)
 *
 * 注意：两种方向的结构在检测时会自动匹配，玩家可以选择任意一个方向搭建
 */
public class PressureMachine extends MultiblockStructure {

    public PressureMachine(Main plugin) {
        super(plugin, "pressure_machine",
                plugin.getConfigManager().getLang("multiblock.pressure_machine.name"),
                createStructure(),
                12);
    }

    /**
     * 创建基础结构模板（X方向，用于GUI显示）
     */
    private static Map<BlockOffset, Material> createStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();

        // Y+1 层（上层）
        structure.put(new BlockOffset(-1, 1, 0), Material.SMOOTH_STONE);   // 左上
        structure.put(new BlockOffset(0, 1, 0), Material.PISTON);          // 中上：活塞(朝下)
        structure.put(new BlockOffset(1, 1, 0), Material.SMOOTH_STONE);    // 右上

        // Y+0 层（中层 - 主方块层）
        structure.put(new BlockOffset(-1, 0, 0), Material.PISTON);         // 左中：活塞(朝右)
        structure.put(new BlockOffset(0, 0, 0), Material.GLASS);           // 中心：玻璃（主方块）
        structure.put(new BlockOffset(1, 0, 0), Material.PISTON);          // 右中：活塞(朝左)

        // Y-1 层（下层）
        structure.put(new BlockOffset(-1, -1, 0), Material.SMOOTH_STONE);  // 左下
        structure.put(new BlockOffset(0, -1, 0), Material.DISPENSER);      // 中下：发射器
        structure.put(new BlockOffset(1, -1, 0), Material.SMOOTH_STONE);   // 右下

        return structure;
    }

    /**
     * 重写结构检查方法，支持X和Y两个方向
     */
    @Override
    public boolean checkStructure(Location centerLocation) {
        if (!ValidationUtils.isValidLocation(centerLocation)) {
            return false;
        }

        try {
            // 先检查X方向结构
            if (checkStructureDirection(centerLocation, true)) {
                return true;
            }

            // 如果X方向不匹配，检查Y方向结构
            return checkStructureDirection(centerLocation, false);

        } catch (Exception e) {
            MessageUtils.logError("Error checking pressure machine structure: " + e.getMessage());
            return false;
        }
    }

    /**
     * 检查特定方向的结构
     * @param centerLocation 中心位置
     * @param isXDirection true为X方向，false为Y方向
     * @return 是否匹配结构
     */
    private boolean checkStructureDirection(Location centerLocation, boolean isXDirection) {
        Map<BlockOffset, Material> structureToCheck = isXDirection ?
                getXDirectionStructure() : getYDirectionStructure();

        MessageUtils.logDebug("检查压力机结构 - 方向: " + (isXDirection ? "X" : "Y") +
                ", 中心位置: " + centerLocation.getBlockX() + "," +
                centerLocation.getBlockY() + "," + centerLocation.getBlockZ());

        for (Map.Entry<BlockOffset, Material> entry : structureToCheck.entrySet()) {
            BlockOffset offset = entry.getKey();
            Material requiredMaterial = entry.getValue();

            Location blockLocation = centerLocation.clone().add(offset.getX(), offset.getY(), offset.getZ());
            Block block = blockLocation.getBlock();

            MessageUtils.logDebug("  检查位置 " + offset + " -> " + blockLocation.getBlockX() + "," +
                    blockLocation.getBlockY() + "," + blockLocation.getBlockZ() +
                    " 需要: " + requiredMaterial + " 实际: " + block.getType());

            if (block == null || block.getType() != requiredMaterial) {
                MessageUtils.logDebug("  结构不匹配于位置 " + offset + " 需要 " + requiredMaterial + " 但找到 " + block.getType());
                return false;
            }
        }

        MessageUtils.logDebug("结构匹配成功 - 方向: " + (isXDirection ? "X" : "Y"));
        return true;
    }

    /**
     * 获取X方向结构定义
     */
    private Map<BlockOffset, Material> getXDirectionStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();

        // Y+1 层（上层）
        structure.put(new BlockOffset(-1, 1, 0), Material.SMOOTH_STONE);
        structure.put(new BlockOffset(0, 1, 0), Material.PISTON);
        structure.put(new BlockOffset(1, 1, 0), Material.SMOOTH_STONE);

        // Y+0 层（中层）
        structure.put(new BlockOffset(-1, 0, 0), Material.PISTON);
        structure.put(new BlockOffset(0, 0, 0), Material.GLASS);
        structure.put(new BlockOffset(1, 0, 0), Material.PISTON);

        // Y-1 层（下层）
        structure.put(new BlockOffset(-1, -1, 0), Material.SMOOTH_STONE);
        structure.put(new BlockOffset(0, -1, 0), Material.DISPENSER);
        structure.put(new BlockOffset(1, -1, 0), Material.SMOOTH_STONE);

        return structure;
    }

    /**
     * 获取Y方向结构定义
     */
    private Map<BlockOffset, Material> getYDirectionStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();

        // Y+1 层（上层）
        structure.put(new BlockOffset(0, 1, -1), Material.SMOOTH_STONE);
        structure.put(new BlockOffset(0, 1, 0), Material.PISTON);
        structure.put(new BlockOffset(0, 1, 1), Material.SMOOTH_STONE);

        // Y+0 层（中层）
        structure.put(new BlockOffset(0, 0, -1), Material.PISTON);
        structure.put(new BlockOffset(0, 0, 0), Material.GLASS);
        structure.put(new BlockOffset(0, 0, 1), Material.PISTON);

        // Y-1 层（下层）
        structure.put(new BlockOffset(0, -1, -1), Material.SMOOTH_STONE);
        structure.put(new BlockOffset(0, -1, 0), Material.DISPENSER);
        structure.put(new BlockOffset(0, -1, 1), Material.SMOOTH_STONE);

        return structure;
    }

    /**
     * 获取当前结构的方向和发射器位置
     * @param centerLocation 中心位置
     * @return 发射器的位置，如果结构无效返回null
     */
    private Location getDispenserLocation(Location centerLocation) {
        // 发射器在两个方向都位于 (0, -1, 0) 相对位置
        Location dispenserLoc = centerLocation.clone().add(0, -1, 0);

        if (dispenserLoc.getBlock().getType() == Material.DISPENSER) {
            // 检查X方向结构
            if (checkStructureDirection(centerLocation, true)) {
                return dispenserLoc;
            }
            // 检查Y方向结构
            if (checkStructureDirection(centerLocation, false)) {
                return dispenserLoc;
            }
        }

        return null;
    }

    @Override
    public void onActivate(Player player, Location location, PlayerInteractEvent event) {
        if (!canUse(player)) {
            MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_NOT_UNLOCKED,
                    "%machine%", getDisplayName());
            return;
        }

        try {
            // 获取发射器位置
            Location dispenserLocation = getDispenserLocation(location);
            if (dispenserLocation == null) {
                MessageUtils.sendLocalizedMessage(player, "multiblock.pressure_machine.error");
                return;
            }

            Block dispenserBlock = dispenserLocation.getBlock();
            Dispenser dispenser = (Dispenser) dispenserBlock.getState();
            Inventory dispenserInv = dispenser.getInventory();

            if (isEmpty(dispenserInv)) {
                sendWelcomeMessages(player);
            } else {
                attemptPressing(player, dispenserInv, location);
            }

        } catch (Exception e) {
            MessageUtils.logError("Error in pressure machine activation: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c处理压力机时发生错误");
        }
    }

    private boolean isEmpty(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (!ItemUtils.isAirOrNull(item)) {
                return false;
            }
        }
        return true;
    }

    private void sendWelcomeMessages(Player player) {
        MessageUtils.sendLocalizedMessage(player, "multiblock.pressure_machine.created");
        MessageUtils.sendLocalizedMessage(player, "multiblock.pressure_machine.hint");
    }

    private void attemptPressing(Player player, Inventory dispenserInv, Location location) {
        // 获取输入物品
        Map<Integer, ItemStack> inputs = getInputsFromDispenser(dispenserInv);

        // 查找匹配的配方
        Optional<Recipe> recipeOpt = plugin.getRecipeManager()
                .findMatchingRecipe(getStructureId(), inputs);

        if (recipeOpt.isEmpty()) {
            MessageUtils.sendLocalizedMessage(player, "multiblock.pressure_machine.no_recipe");
            return;
        }

        Recipe recipe = recipeOpt.get();

        // 检查配方解锁状态
        String recipeUnlockId = "recipe." + recipe.getRecipeId();
        if (!plugin.getUnlockManager().isUnlocked(player, recipeUnlockId)) {
            MessageUtils.sendLocalizedMessage(player, Messages.RECIPE_NOT_UNLOCKED,
                    "%recipe%", recipe.getDisplayName());
            return;
        }

        ItemStack result = recipe.getResult();

        if (!canPressWithCurrentInventory(dispenserInv, recipe, result)) {
            MessageUtils.sendLocalizedMessage(player, "multiblock.pressure_machine.inventory_full");
            return;
        }

        // 执行压制
        if (performPressing(dispenserInv, recipe, result, location)) {
            MessageUtils.sendLocalizedMessage(player, "multiblock.pressure_machine.pressing_success",
                    "%item%", recipe.getDisplayName());
        } else {
            MessageUtils.sendMessage(player, "&c压制失败，请稍后重试");
        }
    }

    private boolean canPressWithCurrentInventory(Inventory inventory, Recipe recipe, ItemStack result) {
        try {
            // 创建库存副本进行模拟
            Inventory simulatedInv = createInventorySnapshot(inventory);

            // 模拟消耗材料
            if (!simulateConsumeIngredients(simulatedInv, recipe)) {
                return false;
            }

            // 尝试添加压制结果
            Map<Integer, ItemStack> leftover = simulatedInv.addItem(result.clone());
            return leftover.isEmpty();

        } catch (Exception e) {
            MessageUtils.logError("Error checking pressing space: " + e.getMessage());
            return false;
        }
    }

    private Inventory createInventorySnapshot(Inventory original) {
        Inventory snapshot = org.bukkit.Bukkit.createInventory(null, original.getSize());
        for (int i = 0; i < original.getSize(); i++) {
            ItemStack item = original.getItem(i);
            if (item != null) {
                snapshot.setItem(i, item.clone());
            }
        }
        return snapshot;
    }

    private boolean simulateConsumeIngredients(Inventory simulatedInv, Recipe recipe) {
        Map<Integer, ItemStack> required = recipe.getIngredients();

        // 验证材料是否足够
        for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
            int slot = entry.getKey();
            ItemStack requiredItem = entry.getValue();

            if (slot >= simulatedInv.getSize()) {
                return false;
            }

            ItemStack current = simulatedInv.getItem(slot);
            if (!hasEnoughMaterial(current, requiredItem)) {
                return false;
            }
        }

        // 模拟消耗材料
        for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
            int slot = entry.getKey();
            ItemStack requiredItem = entry.getValue();

            ItemStack current = simulatedInv.getItem(slot);
            if (current != null) {
                int newAmount = current.getAmount() - requiredItem.getAmount();
                if (newAmount <= 0) {
                    simulatedInv.setItem(slot, null);
                } else {
                    ItemStack newStack = current.clone();
                    newStack.setAmount(newAmount);
                    simulatedInv.setItem(slot, newStack);
                }
            }
        }

        return true;
    }

    private boolean performPressing(Inventory dispenserInv, Recipe recipe, ItemStack result, Location location) {
        try {
            // 播放压制效果
            playPressingEffects(location);

            // 消耗材料
            if (!consumeIngredients(dispenserInv, recipe)) {
                return false;
            }

            // 添加结果物品
            Map<Integer, ItemStack> leftover = dispenserInv.addItem(result.clone());

            // 处理溢出物品（掉落到地面）
            if (!leftover.isEmpty()) {
                Location dropLocation = location.clone().add(0.5, 1.5, 0.5);
                for (ItemStack overflow : leftover.values()) {
                    if (dropLocation.getWorld() != null) {
                        dropLocation.getWorld().dropItemNaturally(dropLocation, overflow);
                    }
                }
                MessageUtils.sendLocalizedMessage(null, "multiblock.pressure_machine.pressing_partial");
            }

            return true;

        } catch (Exception e) {
            MessageUtils.logError("Error performing pressing: " + e.getMessage());
            return false;
        }
    }

    private void playPressingEffects(Location location) {
        if (location.getWorld() != null) {
            // 压制音效 - 模拟活塞运动和压制
            location.getWorld().playSound(location, Sound.BLOCK_PISTON_EXTEND, 1.0f, 0.8f);
            location.getWorld().playSound(location, Sound.BLOCK_ANVIL_USE, 0.7f, 1.2f);

            // 压制粒子效果
            Location effectLocation = location.clone().add(0.5, 0.5, 0.5);

            // 压力效果 - 从四个活塞方向产生粒子
            location.getWorld().spawnParticle(Particle.CLOUD,
                    effectLocation, 12, 0.3, 0.1, 0.3, 0.02);

            // 冲击波效果
            location.getWorld().spawnParticle(Particle.CRIT,
                    effectLocation, 8, 0.4, 0.4, 0.4, 0.1);

            // 模拟活塞同时向中心挤压的效果
            // 上方活塞效果
            location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
                    location.clone().add(0.5, 1.2, 0.5), 3, 0.1, 0.1, 0.1, 0, Material.PISTON.createBlockData());

            // 四周活塞效果（适应两个方向）
            if (checkStructureDirection(location, true)) {
                // X方向的活塞效果
                location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
                        location.clone().add(-0.8, 0.5, 0.5), 3, 0.1, 0.1, 0.1, 0, Material.PISTON.createBlockData());
                location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
                        location.clone().add(1.8, 0.5, 0.5), 3, 0.1, 0.1, 0.1, 0, Material.PISTON.createBlockData());
            } else {
                // Y方向的活塞效果
                location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
                        location.clone().add(0.5, 0.5, -0.8), 3, 0.1, 0.1, 0.1, 0, Material.PISTON.createBlockData());
                location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
                        location.clone().add(0.5, 0.5, 1.8), 3, 0.1, 0.1, 0.1, 0, Material.PISTON.createBlockData());
            }

            // 延迟播放收缩音效
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (location.getWorld() != null) {
                    location.getWorld().playSound(location, Sound.BLOCK_PISTON_CONTRACT, 1.0f, 0.8f);
                }
            }, 10L);
        }
    }

    private boolean consumeIngredients(Inventory dispenserInv, Recipe recipe) {
        Map<Integer, ItemStack> required = recipe.getIngredients();

        // 验证材料是否足够
        for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
            int slot = entry.getKey();
            ItemStack requiredItem = entry.getValue();

            if (slot >= dispenserInv.getSize()) {
                return false;
            }

            ItemStack current = dispenserInv.getItem(slot);
            if (!hasEnoughMaterial(current, requiredItem)) {
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

    private boolean hasEnoughMaterial(ItemStack current, ItemStack required) {
        if (ItemUtils.isAirOrNull(required)) {
            return true;
        }

        if (ItemUtils.isAirOrNull(current)) {
            return false;
        }

        return current.getType() == required.getType() &&
                current.getAmount() >= required.getAmount() &&
                ItemUtils.isSimilar(current, required);
    }

    private Map<Integer, ItemStack> getInputsFromDispenser(Inventory dispenserInv) {
        Map<Integer, ItemStack> inputs = new HashMap<>();

        for (int i = 0; i < Math.min(9, dispenserInv.getSize()); i++) {
            ItemStack item = dispenserInv.getItem(i);
            if (!ItemUtils.isAirOrNull(item)) {
                inputs.put(i, item.clone());
            }
        }

        return inputs;
    }

    @Override
    public boolean canCraft(String recipeId) {
        return plugin.getRecipeManager().getRecipe(recipeId)
                .map(recipe -> getStructureId().equals(recipe.getMultiblockId()))
                .orElse(false);
    }

    /**
     * 获取结构信息，包含两个方向的说明
     */
    @Override
    public Map<String, Object> getStructureInfo() {
        Map<String, Object> info = super.getStructureInfo();
        info.put("directions", "支持X和Y两个方向");
        info.put("note", "可以选择任意一个方向搭建压力机");
        return info;
    }
}