package cn.fandmc.recipe;

import cn.fandmc.Main;
import cn.fandmc.multiblock.MultiblockManager;
import cn.fandmc.multiblock.MultiblockStructure;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeManager {
    private static RecipeManager instance;
    private final Main plugin;
    private final Map<String, Recipe> recipes = new HashMap<>();
    private final Map<String, List<Recipe>> recipesByMultiblock = new HashMap<>();

    private RecipeManager(Main plugin) {
        this.plugin = plugin;
    }

    public static void init(Main plugin) {
        if (instance == null) {
            instance = new RecipeManager(plugin);
        }
    }

    public static RecipeManager getInstance() {
        return instance;
    }

    public void registerRecipe(Recipe recipe) {
        // 验证多方块结构是否存在
        if (!MultiblockManager.getInstance().hasStructure(recipe.getMultiblockId())) {
            plugin.getLogger().warning("配方 " + recipe.getId() +
                    " 需要的多方块结构 " + recipe.getMultiblockId() + " 不存在！");
            return;
        }

        recipes.put(recipe.getId(), recipe);

        // 按多方块结构分类
        recipesByMultiblock.computeIfAbsent(recipe.getMultiblockId(), k -> new ArrayList<>())
                .add(recipe);

        plugin.getLogger().info("已注册配方: " + recipe.getDisplayName() +
                " (绑定到: " + recipe.getMultiblockId() + ")");
    }

    public Recipe getRecipe(String id) {
        return recipes.get(id);
    }

    public List<Recipe> getRecipesForMultiblock(String multiblockId) {
        return recipesByMultiblock.getOrDefault(multiblockId, new ArrayList<>());
    }

    public Recipe findMatchingRecipe(String multiblockId, Map<Integer, ItemStack> inputs) {
        List<Recipe> possibleRecipes = getRecipesForMultiblock(multiblockId);

        for (Recipe recipe : possibleRecipes) {
            if (recipe.matches(inputs)) {
                return recipe;
            }
        }

        return null;
    }

    public Collection<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes.values());
    }

    public List<Recipe> getRecipesByType(RecipeType type) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes.values()) {
            if (recipe.getType() == type) {
                result.add(recipe);
            }
        }
        return result;
    }
}
