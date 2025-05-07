package cn.fandmc.recipe;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public record RecipeInventoryHolder(String recipeId) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
