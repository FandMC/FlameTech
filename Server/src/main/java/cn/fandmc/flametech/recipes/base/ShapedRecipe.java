package cn.fandmc.flametech.recipes.base;

import cn.fandmc.flametech.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 有序配方
 */
public class ShapedRecipe extends Recipe {

    private final String[] pattern;
    private final Map<Character, ItemStack> ingredients;
    private final int width;
    private final int height;

    public ShapedRecipe(String recipeId, String displayName, ItemStack result,
                        String multiblockId, String[] pattern,
                        Map<Character, ItemStack> ingredients, int unlockLevel) {
        super(recipeId, displayName, result, multiblockId, RecipeType.SHAPED, unlockLevel);

        this.pattern = pattern != null ? pattern.clone() : new String[0];
        this.ingredients = ingredients != null ? new HashMap<>(ingredients) : new HashMap<>();
        this.height = this.pattern.length;
        this.width = this.pattern.length > 0 ? this.pattern[0].length() : 0;

        validatePattern();
    }

    public ShapedRecipe(String recipeId, String displayName, ItemStack result,
                        String multiblockId, String[] pattern,
                        Map<Character, ItemStack> ingredients) {
        this(recipeId, displayName, result, multiblockId, pattern, ingredients, 0);
    }

    private void validatePattern() {
        if (pattern.length == 0) {
            throw new IllegalArgumentException("Pattern cannot be empty");
        }

        if (pattern.length > 3) {
            throw new IllegalArgumentException("Pattern height cannot exceed 3");
        }

        for (String row : pattern) {
            if (row.length() > 3) {
                throw new IllegalArgumentException("Pattern width cannot exceed 3");
            }
        }
    }

    @Override
    public boolean matches(Map<Integer, ItemStack> inputs) {
        // 尝试在3x3网格中的所有可能位置匹配图案
        for (int startRow = 0; startRow <= 3 - height; startRow++) {
            for (int startCol = 0; startCol <= 3 - width; startCol++) {
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

                boolean inPattern = row >= startRow && row < startRow + height &&
                        col >= startCol && col < startCol + width;

                if (inPattern) {
                    char patternChar = pattern[row - startRow].charAt(col - startCol);
                    ItemStack required = ingredients.get(patternChar);

                    if (patternChar == ' ') {
                        // 空格表示此位置应该为空
                        if (!ItemUtils.isAirOrNull(input)) {
                            return false;
                        }
                    } else if (!itemMatches(input, required)) {
                        return false;
                    }
                } else {
                    // 图案外的位置应该为空
                    if (!ItemUtils.isAirOrNull(input)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean itemMatches(ItemStack input, ItemStack required) {
        if (required == null) {
            return ItemUtils.isAirOrNull(input);
        }

        if (ItemUtils.isAirOrNull(input)) {
            return false;
        }

        return input.getType() == required.getType() &&
                input.getAmount() >= required.getAmount() &&
                ItemUtils.isSimilar(input, required);
    }

    @Override
    public Map<Integer, ItemStack> getIngredients() {
        Map<Integer, ItemStack> result = new HashMap<>();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                char patternChar = pattern[row].charAt(col);
                if (patternChar != ' ' && ingredients.containsKey(patternChar)) {
                    int slot = row * 3 + col;
                    result.put(slot, ItemUtils.cloneSafely(ingredients.get(patternChar)));
                }
            }
        }

        return result;
    }

    @Override
    public boolean isValid() {
        if (!super.isValid()) {
            return false;
        }

        if (pattern.length == 0 || width == 0) {
            return false;
        }

        // 检查所有模式字符都有对应的材料
        for (String row : pattern) {
            for (char c : row.toCharArray()) {
                if (c != ' ' && !ingredients.containsKey(c)) {
                    return false;
                }
            }
        }

        return true;
    }

    // Getter methods
    public String[] getPattern() {
        return pattern.clone();
    }

    public Map<Character, ItemStack> getIngredientMap() {
        Map<Character, ItemStack> result = new HashMap<>();
        for (Map.Entry<Character, ItemStack> entry : ingredients.entrySet()) {
            result.put(entry.getKey(), ItemUtils.cloneSafely(entry.getValue()));
        }
        return result;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}