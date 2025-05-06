package cn.fandmc.recipe;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Recipe {
    private final String id;
    private final String displayName;

    public Recipe(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public abstract ItemStack getResultPreview();
    public abstract void setupRecipeDisplay(Inventory inv);

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
}
