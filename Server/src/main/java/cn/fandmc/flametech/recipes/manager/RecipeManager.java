package cn.fandmc.flametech.recipes.manager;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.managers.BaseManager;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.recipes.base.RecipeType;
import cn.fandmc.flametech.recipes.impl.*;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 配方管理器
 */
public class RecipeManager extends BaseManager<Recipe> {

    private final Map<String, Recipe> recipes = new ConcurrentHashMap<>();
    private final Map<String, List<Recipe>> recipesByMultiblock = new ConcurrentHashMap<>();
    private final Map<RecipeType, List<Recipe>> recipesByType = new ConcurrentHashMap<>();

    public RecipeManager(Main plugin) {
        super(plugin, "配方管理器");
    }

    /**
     * 注册默认配方
     */
    @Override
    public void registerDefaults() {
        try {
            ToolRecipes.registerAll(this);
            SmeltingFurnaceRecipes.registerAll(this);
            logRegistrationSuccess();
        } catch (Exception e) {
            logRegistrationFailure(e);
            throw e;
        }
    }

    /**
     * 注册配方
     */
    public boolean registerRecipe(Recipe recipe) {
        if (recipe == null) {
            MessageUtils.logWarning("尝试注册空配方");
            return false;
        }

        if (!recipe.isValid()) {
            MessageUtils.logWarning("尝试注册无效的配方: " + recipe.getRecipeId());
            return false;
        }

        String recipeId = recipe.getRecipeId();
        if (recipes.containsKey(recipeId)) {
            MessageUtils.logWarning("ID为 '" + recipeId + "' 的配方已存在");
            return false;
        }

        if (!plugin.getMultiblockManager().hasStructure(recipe.getMultiblockId())) {
            MessageUtils.logWarning("ID为 " + recipeId + " 的配方绑定了无效的多方快结构ID: " + recipe.getMultiblockId());
        }

        recipes.put(recipeId, recipe);

        recipesByMultiblock.computeIfAbsent(recipe.getMultiblockId(), k -> new ArrayList<>())
                .add(recipe);

        recipesByType.computeIfAbsent(recipe.getType(), k -> new ArrayList<>())
                .add(recipe);

        //MessageUtils.logInfo("注册配方: " + recipe.getDisplayName() +
               // " (ID: " + recipeId + ", 多方块结构: " + recipe.getMultiblockId() + ")");
        return true;
    }

    /**
     * 取消注册配方
     */
    public boolean unregisterRecipe(String recipeId) {
        Recipe removed = recipes.remove(recipeId);
        if (removed != null) {
            // 从分组中移除
            recipesByMultiblock.getOrDefault(removed.getMultiblockId(), new ArrayList<>())
                    .remove(removed);
            recipesByType.getOrDefault(removed.getType(), new ArrayList<>())
                    .remove(removed);

            MessageUtils.logDebug("Unregistered recipe: " + recipeId);
            return true;
        }
        return false;
    }

    /**
     * 获取配方
     */
    public Optional<Recipe> getRecipe(String recipeId) {
        return Optional.ofNullable(recipes.get(recipeId));
    }

    /**
     * 获取指定多方块的所有配方
     */
    public List<Recipe> getRecipesForMultiblock(String multiblockId) {
        return new ArrayList<>(recipesByMultiblock.getOrDefault(multiblockId, new ArrayList<>()));
    }

    /**
     * 获取指定类型的所有配方
     */
    public List<Recipe> getRecipesByType(RecipeType type) {
        return new ArrayList<>(recipesByType.getOrDefault(type, new ArrayList<>()));
    }

    /**
     * 查找匹配的配方
     */
    public Optional<Recipe> findMatchingRecipe(String multiblockId, Map<Integer, ItemStack> inputs) {
        List<Recipe> possibleRecipes = getRecipesForMultiblock(multiblockId);

        for (Recipe recipe : possibleRecipes) {
            try {
                if (recipe.matches(inputs)) {
                    return Optional.of(recipe);
                }
            } catch (Exception e) {
                MessageUtils.logWarning("Error checking recipe match for " + recipe.getRecipeId() + ": " + e.getMessage());
            }
        }

        return Optional.empty();
    }

    /**
     * 获取所有配方
     */
    public Collection<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes.values());
    }

    /**
     * 根据关键词搜索配方
     */
    public List<Recipe> searchRecipes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(recipes.values());
        }

        String lowerKeyword = keyword.toLowerCase();
        return recipes.values().stream()
                .filter(recipe -> recipe.getDisplayName().toLowerCase().contains(lowerKeyword) ||
                        recipe.getRecipeId().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * 获取配方统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_recipes", recipes.size());
        stats.put("recipes_by_type", recipesByType.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry -> entry.getValue().size()
                )));
        stats.put("recipes_by_multiblock", recipesByMultiblock.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                )));
        return stats;
    }

    /**
     * 验证所有配方
     */
    public List<String> validateAllRecipes() {
        List<String> errors = new ArrayList<>();

        for (Recipe recipe : recipes.values()) {
            try {
                if (!recipe.isValid()) {
                    errors.add("Invalid recipe: " + recipe.getRecipeId());
                }
            } catch (Exception e) {
                errors.add("Error validating recipe " + recipe.getRecipeId() + ": " + e.getMessage());
            }
        }

        return errors;
    }

    /**
     * 清空所有配方
     */
    @Override
    public void clearAll() {
        recipes.clear();
        recipesByMultiblock.clear();
        recipesByType.clear();
        logClearDebug();
    }

    @Override
    public int getRegisteredCount() {
        return recipes.size();
    }

    /**
     * 获取配方数量
     */
    public int getRecipeCount() {
        return recipes.size();
    }


}