package cn.fandmc.recipe;

import org.bukkit.inventory.ItemStack;
import cn.fandmc.util.LangUtil;

import java.util.HashMap;
import java.util.Map;

public final class RecipeRegistry {
    private static final Map<String, Recipe> RECIPES = new HashMap<>();

    private RecipeRegistry() {}

    public static void register(Recipe recipe) {
        if (RECIPES.containsKey(recipe.getId())) {
            throw new IllegalArgumentException(LangUtil.get("Recipe.Error.DuplicateID") + recipe.getId());
        }
        RECIPES.put(recipe.getId(), recipe);
    }

    public static Recipe getRecipeByResult(ItemStack item) {
        if (item == null) return null;
        for (Recipe recipe : RECIPES.values()) {
            ItemStack preview = recipe.getResultPreview();
            if (preview.isSimilar(item)) {
                return recipe;
            }
        }
        return null;
    }

    public static Recipe getRecipe(String id) {
        return RECIPES.get(id);
    }
}
