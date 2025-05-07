package cn.fandmc.recipe;

import cn.fandmc.Main;

import java.util.HashMap;
import java.util.Map;

public final class RecipeRegistry {
    private static final Map<String, Recipe> RECIPES = new HashMap<>();

    private RecipeRegistry() {}

    public static void register(Recipe recipe) {
        if (RECIPES.containsKey(recipe.getId())) {
            throw new IllegalArgumentException(getlang("Recipe.Error.DuplicateID") + recipe.getId());
        }
        RECIPES.put(recipe.getId(), recipe);
    }

    public static Recipe getRecipe(String id) {
        return RECIPES.get(id);
    }

    public static String getlang(String config){
        return Main.getconfig().color(config);
    }
}
