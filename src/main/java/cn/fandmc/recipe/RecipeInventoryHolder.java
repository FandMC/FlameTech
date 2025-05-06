package cn.fandmc.recipe;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class RecipeInventoryHolder implements InventoryHolder {
    private final String recipeId;

    public RecipeInventoryHolder(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
