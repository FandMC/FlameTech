package cn.fandmc.recipe.impl;

import cn.fandmc.gui.item.StrangeTool.ExplosivePickaxe;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.util.LangUtil;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ExplosivePickaxeRecipe extends Recipe {

    public ExplosivePickaxeRecipe() {
        super("explosive_pickaxe",
                LangUtil.get("Item.ExplosivePickaxe.Name"),
                true,
                "enhanced_workbench");

        addIngredient(0, Material.DIAMOND, 1)
                .addIngredient(1, Material.TNT, 1)
                .addIngredient(2, Material.DIAMOND, 1)
                .addIngredient(4, Material.STICK, 1)
                .addIngredient(7, Material.STICK, 1);
    }

    @Override
    public ItemStack getResultPreview() {
        return ExplosivePickaxe.createExplosivePickaxe();
    }

    @Override
    public void setupRecipeDisplay(Inventory inv) {
        ingredients.forEach(inv::setItem);
        inv.setItem(16, getResultPreview());
    }
}
