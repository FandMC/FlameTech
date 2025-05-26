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

/**
 * 增强工作台多方块结构
 */
public class EnhancedCraftingTable extends MultiblockStructure {

    public EnhancedCraftingTable(Main plugin) {
        super(plugin, ItemKeys.ID_ENHANCED_CRAFTING_TABLE,
                plugin.getConfigManager().getLang("multiblock.enhanced_crafting_table.name"),
                createStructure(),
                10);
    }

    private static Map<BlockOffset, Material> createStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();

        // 主方块：工作台
        structure.put(new BlockOffset(0, 0, 0), Material.CRAFTING_TABLE);

        // 下方：发射器
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

        // 检查空间
        ItemStack result = recipe.getResult();
        if (!canAddItemToInventory(dispenserInv, result)) {
            MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_ENHANCED_CRAFTING_INVENTORY_FULL);
            return;
        }

        // 消耗材料
        if (!consumeIngredients(dispenserInv, recipe)) {
            MessageUtils.sendMessage(player, "&c材料不足或消耗失败！");
            return;
        }

        // 添加结果
        Map<Integer, ItemStack> leftover = dispenserInv.addItem(result);

        if (leftover.isEmpty()) {
            MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_ENHANCED_CRAFTING_CRAFT_SUCCESS,
                    "%item%", recipe.getDisplayName());
        } else {
            MessageUtils.sendMessage(player, "&e合成完成，但部分物品可能掉落在地上。");
        }
    }

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

    private boolean canAddItemToInventory(Inventory inventory, ItemStack item) {
        if (ItemUtils.isAirOrNull(item)) {
            return true;
        }

        // 创建临时库存副本来测试
        Inventory tempInv = org.bukkit.Bukkit.createInventory(null, inventory.getSize());
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack existing = inventory.getItem(i);
            if (existing != null) {
                tempInv.setItem(i, existing.clone());
            }
        }

        Map<Integer, ItemStack> leftover = tempInv.addItem(item.clone());
        return leftover.isEmpty();
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

    @Override
    public boolean canCraft(String recipeId) {
        return plugin.getRecipeManager().getRecipe(recipeId)
                .map(recipe -> getStructureId().equals(recipe.getMultiblockId()))
                .orElse(false);
    }
}