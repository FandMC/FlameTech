package cn.fandmc.recipe.impl;

import cn.fandmc.config.Config;
import cn.fandmc.gui.item.StrangeTool.SmeltingPickaxe;
import cn.fandmc.recipe.Recipe;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SmeltingPickaxeRecipe extends Recipe {

    public SmeltingPickaxeRecipe() {
        super("smelting_pickaxe",
                Config.ITEM_SMELTINGPICKAXE_NAME,
                "enhanced_workbench");

        addIngredient(0, Material.IRON_INGOT, 1)
                .addIngredient(1, Material.FURNACE, 1)
                .addIngredient(2, Material.IRON_INGOT, 1)
                .addIngredient(4, Material.STICK, 1)
                .addIngredient(7, Material.STICK, 1);
    }

    @Override
    public ItemStack getResultPreview() {
        return SmeltingPickaxe.createSmeltingPickaxe();
    }

    @Override
    public void setupRecipeDisplay(Inventory inv) {
        ingredients.forEach(inv::setItem);
        inv.setItem(16, getResultPreview());
    }
}
