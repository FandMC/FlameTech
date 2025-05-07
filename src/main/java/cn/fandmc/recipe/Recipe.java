package cn.fandmc.recipe;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Recipe {
    private final String id;
    private final String displayName;
    private final boolean isStructureRecipe;
    private final String requiredStructureId;

    public Recipe(String id, String displayName, boolean isStructure, String requiredStructureId) {
        this.id = id;
        this.displayName = displayName;
        this.isStructureRecipe = isStructure;
        this.requiredStructureId = requiredStructureId;
    }

    public boolean isStructureRecipe() {
        return isStructureRecipe;
    }

    public boolean requiresStructure() {
        return requiredStructureId != null;
    }

    public String getRequiredStructureId() {
        return requiredStructureId;
    }
    public abstract ItemStack getResultPreview();
    public abstract void setupRecipeDisplay(Inventory inv);

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
}
