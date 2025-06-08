// ============= 优化后的 EnhancedCraftingTable.java =============
package cn.fandmc.flametech.multiblock.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.multiblock.base.BlockOffset;
import cn.fandmc.flametech.multiblock.base.MultiblockStructure;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.utils.ItemUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnhancedCraftingTable extends MultiblockStructure {

    public EnhancedCraftingTable(Main plugin) {
        super(plugin, ItemKeys.ID_ENHANCED_CRAFTING_TABLE,
                plugin.getConfigManager().getLang("multiblock.enhanced_crafting_table.name"),
                createStructure(),
                10);
    }

    private static Map<BlockOffset, Material> createStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();
        structure.put(new BlockOffset(0, 0, 0), Material.CRAFTING_TABLE);
        structure.put(new BlockOffset(0, -1, 0), Material.DISPENSER);
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
            Block dispenserBlock = location.clone().add(0, -1, 0).getBlock();

            if (dispenserBlock.getType() != Material.DISPENSER) {
                MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_ENHANCED_CRAFTING_ERROR);
                return;
            }

            Dispenser dispenser = (Dispenser) dispenserBlock.getState();
            Inventory dispenserInv = dispenser.getInventory();

            if (isEmpty(dispenserInv)) {
                sendWelcomeMessages(player);
            } else {
                attemptAutoCrafting(player, dispenserInv);
            }

        } catch (Exception e) {
            MessageUtils.logError("Error in enhanced crafting table activation: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c处理多方块结构时发生错误");
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
        MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_ENHANCED_CRAFTING_CREATED);
        MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_ENHANCED_CRAFTING_HINT);
    }

    private void attemptAutoCrafting(Player player, Inventory dispenserInv) {
        // 获取输入物品
        Map<Integer, ItemStack> inputs = getInputsFromDispenser(dispenserInv);

        // 查找匹配的配方
        Optional<Recipe> recipeOpt = plugin.getRecipeManager()
                .findMatchingRecipe(getStructureId(), inputs);

        if (recipeOpt.isEmpty()) {
            MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_ENHANCED_CRAFTING_NO_RECIPE);
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

        if (!canCraftWithCurrentInventory(dispenserInv, recipe, result)) {
            MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_ENHANCED_CRAFTING_INVENTORY_FULL);
            return;
        }

        // 执行合成
        if (performCrafting(dispenserInv, recipe, result)) {
            MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_ENHANCED_CRAFTING_CRAFT_SUCCESS,
                    "%item%", recipe.getDisplayName());
        } else {
            MessageUtils.sendMessage(player, "&c合成失败，请稍后重试");
        }
    }

    /**
     * 优化后的空间检测：考虑合成后材料消耗和结果物品的空间需求
     */
    private boolean canCraftWithCurrentInventory(Inventory inventory, Recipe recipe, ItemStack result) {
        try {
            // 创建库存副本进行模拟
            Inventory simulatedInv = createInventorySnapshot(inventory);

            // 模拟消耗材料
            if (!simulateConsumeIngredients(simulatedInv, recipe)) {
                return false; // 材料不足
            }

            // 尝试添加合成结果
            Map<Integer, ItemStack> leftover = simulatedInv.addItem(result.clone());

            // 如果没有剩余物品，说明可以完全放入
            return leftover.isEmpty();

        } catch (Exception e) {
            MessageUtils.logError("Error checking crafting space: " + e.getMessage());
            return false;
        }
    }

    /**
     * 创建库存快照用于模拟
     */
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

    /**
     * 模拟消耗配方材料，不实际修改库存
     */
    private boolean simulateConsumeIngredients(Inventory simulatedInv, Recipe recipe) {
        Map<Integer, ItemStack> required = recipe.getIngredients();

        // 首先验证是否有足够材料
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

    /**
     * 执行实际的合成操作
     */
    private boolean performCrafting(Inventory dispenserInv, Recipe recipe, ItemStack result) {
        try {
            // 消耗材料
            if (!consumeIngredients(dispenserInv, recipe)) {
                return false;
            }

            // 添加结果物品
            Map<Integer, ItemStack> leftover = dispenserInv.addItem(result.clone());

            // 处理溢出物品（掉落到地面）
            if (!leftover.isEmpty()) {
                Location dropLocation = ((Dispenser) dispenserInv.getHolder()).getLocation().add(0.5, 1.5, 0.5);
                for (ItemStack overflow : leftover.values()) {
                    if (dropLocation.getWorld() != null) {
                        dropLocation.getWorld().dropItemNaturally(dropLocation, overflow);
                    }
                }
                MessageUtils.sendLocalizedMessage(null, Messages.MULTIBLOCK_ENHANCED_CRAFTING_CRAFT_PARTIAL);
            }

            return true;

        } catch (Exception e) {
            MessageUtils.logError("Error performing crafting: " + e.getMessage());
            return false;
        }
    }

    /**
     * 实际消耗配方材料
     */
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

    /**
     * 检查是否有足够的材料
     */
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

    /**
     * 获取发射器中的输入物品映射
     */
    private Map<Integer, ItemStack> getInputsFromDispenser(Inventory dispenserInv) {
        Map<Integer, ItemStack> inputs = new HashMap<>();

        // 将发射器的9个槽位映射到3x3网格
        for (int i = 0; i < Math.min(9, dispenserInv.getSize()); i++) {
            ItemStack item = dispenserInv.getItem(i);
            if (!ItemUtils.isAirOrNull(item)) {
                inputs.put(i, item.clone());
            }
        }

        return inputs;
    }

    /**
     * 获取发射器当前的空间统计信息（调试用）
     */
    private InventorySpaceInfo analyzeInventorySpace(Inventory inventory) {
        int emptySlots = 0;
        int partialSlots = 0;
        int fullSlots = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (ItemUtils.isAirOrNull(item)) {
                emptySlots++;
            } else if (item.getAmount() < item.getMaxStackSize()) {
                partialSlots++;
            } else {
                fullSlots++;
            }
        }

        return new InventorySpaceInfo(emptySlots, partialSlots, fullSlots);
    }

    @Override
    public boolean canCraft(String recipeId) {
        return plugin.getRecipeManager().getRecipe(recipeId)
                .map(recipe -> getStructureId().equals(recipe.getMultiblockId()))
                .orElse(false);
    }

    /**
     * 库存空间信息类（用于调试和优化）
     */
    private static class InventorySpaceInfo {
        final int emptySlots;
        final int partialSlots;
        final int fullSlots;

        InventorySpaceInfo(int emptySlots, int partialSlots, int fullSlots) {
            this.emptySlots = emptySlots;
            this.partialSlots = partialSlots;
            this.fullSlots = fullSlots;
        }

        boolean hasSpace() {
            return emptySlots > 0 || partialSlots > 0;
        }

        @Override
        public String toString() {
            return String.format("InventorySpace{empty=%d, partial=%d, full=%d}",
                    emptySlots, partialSlots, fullSlots);
        }
    }
}