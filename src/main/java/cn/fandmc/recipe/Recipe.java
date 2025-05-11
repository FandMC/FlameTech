package cn.fandmc.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class Recipe {
    private final String id;
    private final String displayName;

    private final String requiredStructureId;
    public final Map<Integer, ItemStack> ingredients = new HashMap<>();

    public Recipe(String id, String displayName, String requiredStructureId) {
        this.id = id;
        this.displayName = displayName;
        this.requiredStructureId = requiredStructureId;
    }

    public boolean requiresStructure() {
        return requiredStructureId != null;
    }

    public Recipe addIngredient(int slot, Material material, int amount) {
        ingredients.put(slot, new ItemStack(material, amount));
        return this;
    }

    public boolean matches(Inventory inventory) {
        for (Map.Entry<Integer, ItemStack> entry : ingredients.entrySet()) {
            int slot = entry.getKey();
            ItemStack current = inventory.getItem(slot);
            ItemStack required = entry.getValue();

            if (current == null ||
                    current.getType() != required.getType() ||
                    current.getAmount() < required.getAmount()) {
                return false;
            }
        }
        return true;
    }

    public String getRequiredStructureId() {
        return requiredStructureId;
    }

    public abstract ItemStack getResultPreview();
    public abstract void setupRecipeDisplay(Inventory inv);

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
}
