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
 * 筛矿机多方块结构
 * 结构：
 * 0 t 0  (Y+1)
 * 0 h 0  (Y+0) - 主方块层
 * 0 l 0  (Y-1)
 *
 * h = 木栅栏 (主方块)
 * t = 发射器
 * l = 炼药锅
 */
public class OreSifter extends MultiblockStructure {

    public OreSifter(Main plugin) {
        super(plugin, "ore_sifter",
                plugin.getConfigManager().getLang("multiblock.ore_sifter.name"),
                createStructure(),
                8);
    }

    private static Map<BlockOffset, Material> createStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();

        // Y+1 层：发射器
        structure.put(new BlockOffset(0, 1, 0), Material.DISPENSER);

        // Y+0 层：木栅栏（主方块）
        structure.put(new BlockOffset(0, 0, 0), Material.OAK_FENCE);

        // Y-1 层：炼药锅
        structure.put(new BlockOffset(0, -1, 0), Material.CAULDRON);

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
            // 获取发射器
            Block dispenserBlock = location.clone().add(0, 1, 0).getBlock();
            if (dispenserBlock.getType() != Material.DISPENSER) {
                MessageUtils.sendLocalizedMessage(player, "multiblock.ore_sifter.error");
                return;
            }

            Dispenser dispenser = (Dispenser) dispenserBlock.getState();
            Inventory dispenserInv = dispenser.getInventory();

            if (isEmpty(dispenserInv)) {
                sendWelcomeMessages(player);
            } else {
                attemptSifting(player, dispenserInv, location);
            }

        } catch (Exception e) {
            MessageUtils.logError("Error in ore sifter activation: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c处理筛矿机时发生错误");
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
        MessageUtils.sendLocalizedMessage(player, "multiblock.ore_sifter.created");
        MessageUtils.sendLocalizedMessage(player, "multiblock.ore_sifter.hint");
    }

    private void attemptSifting(Player player, Inventory dispenserInv, Location location) {
        // 获取输入物品
        Map<Integer, ItemStack> inputs = getInputsFromDispenser(dispenserInv);

        // 查找匹配的配方
        Optional<Recipe> recipeOpt = plugin.getRecipeManager()
                .findMatchingRecipe(getStructureId(), inputs);

        if (recipeOpt.isEmpty()) {
            MessageUtils.sendLocalizedMessage(player, "multiblock.ore_sifter.no_recipe");
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

        if (!canSiftWithCurrentInventory(dispenserInv, recipe, result)) {
            MessageUtils.sendLocalizedMessage(player, "multiblock.ore_sifter.inventory_full");
            return;
        }

        // 执行筛选
        if (performSifting(dispenserInv, recipe, result, location)) {
            MessageUtils.sendLocalizedMessage(player, "multiblock.ore_sifter.sifting_success",
                    "%item%", recipe.getDisplayName());
        } else {
            MessageUtils.sendMessage(player, "&c筛选失败，请稍后重试");
        }
    }

    private boolean canSiftWithCurrentInventory(Inventory inventory, Recipe recipe, ItemStack result) {
        try {
            // 创建库存副本进行模拟
            Inventory simulatedInv = createInventorySnapshot(inventory);

            // 模拟消耗材料
            if (!simulateConsumeIngredients(simulatedInv, recipe)) {
                return false;
            }

            // 尝试添加筛选结果
            Map<Integer, ItemStack> leftover = simulatedInv.addItem(result.clone());
            return leftover.isEmpty();

        } catch (Exception e) {
            MessageUtils.logError("Error checking sifting space: " + e.getMessage());
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

    private boolean performSifting(Inventory dispenserInv, Recipe recipe, ItemStack result, Location location) {
        try {
            // 播放筛选效果
            playSiftingEffects(location);

            // 消耗材料
            if (!consumeIngredients(dispenserInv, recipe)) {
                return false;
            }

            // 添加结果物品到炼药锅位置（实际是掉落）
            Location dropLocation = location.clone().add(0.5, -0.5, 0.5);
            if (dropLocation.getWorld() != null) {
                dropLocation.getWorld().dropItemNaturally(dropLocation, result.clone());
            }

            return true;

        } catch (Exception e) {
            MessageUtils.logError("Error performing sifting: " + e.getMessage());
            return false;
        }
    }

    private void playSiftingEffects(Location location) {
        if (location.getWorld() != null) {
            // 筛选音效
            location.getWorld().playSound(location, Sound.BLOCK_SAND_BREAK, 1.0f, 1.2f);
            location.getWorld().playSound(location, Sound.BLOCK_GRAVEL_BREAK, 1.0f, 0.8f);

            // 筛选粒子效果
            Location effectLocation = location.clone().add(0.5, 0.5, 0.5);
            location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
                    effectLocation, 15, 0.3, 0.3, 0.3, 0, Material.SAND.createBlockData());
            location.getWorld().spawnParticle(Particle.ITEM_SNOWBALL,
                    effectLocation, 10, 0.2, 0.2, 0.2, 0.1, new ItemStack(Material.GRAVEL));
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
}