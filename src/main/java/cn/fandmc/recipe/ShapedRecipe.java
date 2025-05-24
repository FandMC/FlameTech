package cn.fandmc.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class ShapedRecipe extends Recipe {
    private final String[] pattern;
    private final Map<Character, ItemStack> ingredients;

    public ShapedRecipe(String id, String displayName, ItemStack result, String multiblockId,
                        String[] pattern, Map<Character, ItemStack> ingredients) {
        super(id, displayName, result, multiblockId);
        this.pattern = pattern;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(Map<Integer, ItemStack> inputs) {
        for (int startRow = 0; startRow <= 3 - pattern.length; startRow++) {
            for (int startCol = 0; startCol <= 3 - pattern[0].length(); startCol++) {
                if (matchesAt(inputs, startRow, startCol)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesAt(Map<Integer, ItemStack> inputs, int startRow, int startCol) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slot = row * 3 + col;
                ItemStack input = inputs.get(slot);

                boolean inPattern = row >= startRow && row < startRow + pattern.length &&
                        col >= startCol && col < startCol + pattern[0].length();

                if (inPattern) {
                    char c = pattern[row - startRow].charAt(col - startCol);
                    ItemStack required = ingredients.get(c);

                    if (c == ' ') {
                        if (input != null && input.getType() != Material.AIR) return false;
                    } else if (!itemMatches(input, required)) {
                        return false;
                    }
                } else {
                    if (input != null && input.getType() != Material.AIR) return false;
                }
            }
        }
        return true;
    }

    private boolean itemMatches(ItemStack input, ItemStack required) {
        if (required == null) return input == null || input.getType() == Material.AIR;
        if (input == null || input.getType() == Material.AIR) return false;
        return input.getType() == required.getType() &&
                input.getAmount() >= required.getAmount();
    }

    @Override
    public Map<Integer, ItemStack> getIngredients() {
        Map<Integer, ItemStack> result = new HashMap<>();
        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[row].length(); col++) {
                char c = pattern[row].charAt(col);
                if (c != ' ' && ingredients.containsKey(c)) {
                    result.put(row * 3 + col, ingredients.get(c).clone());
                }
            }
        }
        return result;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }

    public String[] getPattern() { return pattern.clone(); }
    public Map<Character, ItemStack> getIngredientMap() { return new HashMap<>(ingredients); }
}