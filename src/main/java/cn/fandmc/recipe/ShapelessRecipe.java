package cn.fandmc.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class ShapelessRecipe extends Recipe {
    private final List<ItemStack> ingredients;

    public ShapelessRecipe(String id, String displayName, ItemStack result,
                           String multiblockId, List<ItemStack> ingredients) {
        super(id, displayName, result, multiblockId);
        this.ingredients = new ArrayList<>(ingredients);
    }

    @Override
    public boolean matches(Map<Integer, ItemStack> inputs) {
        List<ItemStack> inputList = new ArrayList<>();
        for (ItemStack input : inputs.values()) {
            if (input != null && input.getType() != Material.AIR) {
                inputList.add(input.clone());
            }
        }

        if (inputList.size() != ingredients.size()) return false;

        List<ItemStack> requiredCopy = new ArrayList<>(ingredients);
        for (ItemStack input : inputList) {
            boolean found = false;
            for (int i = 0; i < requiredCopy.size(); i++) {
                ItemStack required = requiredCopy.get(i);
                if (input.getType() == required.getType() &&
                        input.getAmount() >= required.getAmount()) {
                    requiredCopy.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        return requiredCopy.isEmpty();
    }

    @Override
    public Map<Integer, ItemStack> getIngredients() {
        Map<Integer, ItemStack> result = new HashMap<>();
        for (int i = 0; i < ingredients.size(); i++) {
            result.put(i, ingredients.get(i).clone());
        }
        return result;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS;
    }

    public List<ItemStack> getIngredientList() {
        return new ArrayList<>(ingredients);
    }
}