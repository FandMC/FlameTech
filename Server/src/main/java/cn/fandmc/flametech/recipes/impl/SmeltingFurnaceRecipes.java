package cn.fandmc.flametech.recipes.impl;

import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import cn.fandmc.flametech.recipes.impl.smelting.*;
import cn.fandmc.flametech.utils.MessageUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 治炼炉配方管理器 - 优化版
 */
public final class SmeltingFurnaceRecipes {

    private static final Map<String, RecipeRegistrar> RECIPE_MAP = new LinkedHashMap<>();

    static {
        // 基础金属粉末熔炼配方
        registerRecipe("iron_dust", new IronDustSmeltingRecipe());
        registerRecipe("copper_dust", new CopperDustSmeltingRecipe());
        registerRecipe("magnesium_dust", new MagnesiumDustSmeltingRecipe());
        registerRecipe("silver_dust", new SilverDustSmeltingRecipe());
        registerRecipe("zinc_dust", new ZincDustSmeltingRecipe());
        registerRecipe("aluminum_dust", new AluminumDustSmeltingRecipe());
        registerRecipe("tin_dust", new TinDustSmeltingRecipe());

        // 金粉熔炼配方
        registerRecipe("gold_dust", new GoldDustSmeltingRecipe());

        // 金锭纯度提升配方（10% -> 100%）
        for (int purity = 10; purity < 100; purity += 10) {
            String key = "gold_purity_" + purity + "_to_" + (purity + 10);
            registerRecipe(key, new GoldIngotPurityUpgradeRecipe(purity));
        }
    }

    /**
     * 注册配方到映射
     */
    private static void registerRecipe(String key, RecipeRegistrar recipe) {
        RECIPE_MAP.put(key, recipe);
    }

    /**
     * 注册所有治炼炉配方
     */
    public static void registerAll(RecipeManager recipeManager) {
        int successCount = 0;
        int failCount = 0;

        for (Map.Entry<String, RecipeRegistrar> entry : RECIPE_MAP.entrySet()) {
            String key = entry.getKey();
            RecipeRegistrar registrar = entry.getValue();

            try {
                if (registrar.register(recipeManager)) {
                    successCount++;
                    MessageUtils.logDebug("成功注册治炼炉配方: " + registrar.getRecipeName());
                } else {
                    failCount++;
                    MessageUtils.logWarning("注册治炼炉配方失败: " + registrar.getRecipeName());
                }
            } catch (Exception e) {
                failCount++;
                MessageUtils.logError("注册配方时发生异常 [" + key + "]: " + e.getMessage());
            }
        }

        MessageUtils.logInfo("治炼炉配方注册完成 - 成功: " + successCount + ", 失败: " + failCount);
    }

    /**
     * 获取所有配方注册器（用于调试）
     */
    public static Map<String, RecipeRegistrar> getAllRecipes() {
        return new LinkedHashMap<>(RECIPE_MAP);
    }

    /**
     * 获取配方数量
     */
    public static int getRecipeCount() {
        return RECIPE_MAP.size();
    }

    private SmeltingFurnaceRecipes() {
        throw new UnsupportedOperationException("Utility class");
    }
}