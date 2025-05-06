package cn.fandmc.recipe;

import java.util.HashMap;
import java.util.Map;

public final class RecipeReg {
    private static final Map<String, Recipe> RECIPES = new HashMap<>();

    private RecipeReg() {}

    public static void register(Recipe recipe) {
        if (RECIPES.containsKey(recipe.getId())) {
            throw new IllegalArgumentException("重复的配方ID: " + recipe.getId());
        }
        RECIPES.put(recipe.getId(), recipe);
    }

    public static Recipe getRecipe(String id) {
        return RECIPES.get(id);
    }

    public static Map<String, Recipe> getAllRecipes() {
        return new HashMap<>(RECIPES);
    }
}
