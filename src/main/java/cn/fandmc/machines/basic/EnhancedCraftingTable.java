package cn.fandmc.machines.basic;

import cn.fandmc.Main;
import cn.fandmc.multiblock.BlockOffset;
import cn.fandmc.multiblock.MultiblockStructure;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeManager;
import cn.fandmc.unlock.UnlockManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnhancedCraftingTable extends MultiblockStructure {

    public EnhancedCraftingTable() {
        super("enhanced_crafting_table",
                Main.getInstance().getConfigManager().getLang("multiblock.enhanced_crafting_table.name"),
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
    public void onActivate(Player player, Location location) {
        String unlockId = "multiblock." + getId();
        if (!UnlockManager.getInstance().isUnlocked(player, unlockId)) {
            player.sendMessage(Main.getInstance().getConfigManager().getLang("multiblock.not_unlocked")
                    .replace("%machine%", getDisplayName()));
            return;
        }

        Block dispenserBlock = location.clone().add(0, -1, 0).getBlock();

        if (dispenserBlock.getType() != Material.DISPENSER) {
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.error"));
            return;
        }

        Dispenser dispenser = (Dispenser) dispenserBlock.getState();
        Inventory dispenserInv = dispenser.getInventory();

        boolean hasItems = false;
        for (ItemStack item : dispenserInv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                hasItems = true;
                break;
            }
        }

        if (!hasItems) {
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.created"));
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.hint"));
        } else {
            // 尝试自动合成
            attemptAutoCrafting(player, dispenserInv);
        }
    }

    private void attemptAutoCrafting(Player player, Inventory dispenserInv) {
        // 获取发射器中的物品作为输入
        Map<Integer, ItemStack> inputs = getInputsFromDispenser(dispenserInv);

        // 查找匹配的配方
        Recipe recipe = RecipeManager.getInstance().findMatchingRecipe("enhanced_crafting_table", inputs);

        if (recipe == null) {
            // 没有匹配的配方
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.no_recipe"));
            return;
        }

        // 检查发射器是否有空位放置结果
        ItemStack result = recipe.getResult();
        if (!canAddItemToInventory(dispenserInv, result)) {
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.inventory_full"));
            return;
        }

        // 消耗材料
        if (!consumeIngredients(dispenserInv, recipe)) {
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.consume_failed"));
            return;
        }

        // 添加结果到发射器
        Map<Integer, ItemStack> leftover = dispenserInv.addItem(result);

        if (leftover.isEmpty()) {
            // 合成成功
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.craft_success")
                    .replace("%item%", recipe.getDisplayName()));
        } else {
            // 这种情况理论上不应该发生，因为我们已经检查过空间
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.craft_partial"));
        }
    }

    private Map<Integer, ItemStack> getInputsFromDispenser(Inventory dispenserInv) {
        Map<Integer, ItemStack> inputs = new HashMap<>();

        for (int i = 0; i < 9 && i < dispenserInv.getSize(); i++) {
            ItemStack item = dispenserInv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                inputs.put(i, item);
            }
        }

        return inputs;
    }

    private boolean canAddItemToInventory(Inventory inventory, ItemStack item) {
        // 创建临时库存副本来测试
        Inventory tempInv = org.bukkit.Bukkit.createInventory(null, inventory.getSize());
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack existing = inventory.getItem(i);
            if (existing != null) {
                tempInv.setItem(i, existing.clone());
            }
        }

        // 尝试添加物品
        Map<Integer, ItemStack> leftover = tempInv.addItem(item.clone());
        return leftover.isEmpty();
    }

    private boolean consumeIngredients(Inventory dispenserInv, Recipe recipe) {
        Map<Integer, ItemStack> required = recipe.getIngredients();

        // 先检查是否有足够的材料
        for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
            int slot = entry.getKey();
            ItemStack requiredItem = entry.getValue();

            if (slot >= dispenserInv.getSize()) {
                return false;
            }

            ItemStack current = dispenserInv.getItem(slot);
            if (current == null ||
                    current.getType() != requiredItem.getType() ||
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

    @Override
    public boolean canCraft(String recipeId) {
        return true;
    }
}