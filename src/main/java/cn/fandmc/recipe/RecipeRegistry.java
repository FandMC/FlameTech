package cn.fandmc.recipe;

import cn.fandmc.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import cn.fandmc.util.LangUtil;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
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

    public static Collection<Recipe> getAllRecipes() {
        return RECIPES.values();
    }

    public static Recipe getRecipeByResult(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "tool_type");
        String toolType = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (toolType != null) {
            System.out.println("[DEBUG] 查找配方: " + toolType);
            Recipe recipe = RECIPES.get(toolType);
            System.out.println("[DEBUG] 找到配方: " + (recipe != null ? recipe.getId() : "null"));
            return recipe;
        }
        return null;
    }

    public static Recipe getRecipe(String id) {
        return RECIPES.get(id);
    }
}
