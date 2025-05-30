package cn.fandmc.flametech.recipes.impl;

import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import cn.fandmc.flametech.recipes.impl.smelting.*;
import cn.fandmc.flametech.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 治炼炉配方管理器
 */
public final class SmeltingFurnaceRecipes {

    private static final List<RecipeRegistrar> RECIPE_REGISTRARS = new ArrayList<>();

    static {
        // 基础金属粉末熔炼配方
        RECIPE_REGISTRARS.add(new IronDustSmeltingRecipe());
        RECIPE_REGISTRARS.add(new CopperDustSmeltingRecipe());
        RECIPE_REGISTRARS.add(new MagnesiumDustSmeltingRecipe());
        RECIPE_REGISTRARS.add(new SilverDustSmeltingRecipe());
        RECIPE_REGISTRARS.add(new ZincDustSmeltingRecipe());
        RECIPE_REGISTRARS.add(new AluminumDustSmeltingRecipe());
        RECIPE_REGISTRARS.add(new TinDustSmeltingRecipe());

        // 金粉熔炼配方
        RECIPE_REGISTRARS.add(new GoldDustSmeltingRecipe());

        // 金锭纯度提升配方
        for (int purity = 10; purity < 100; purity += 10) {
            RECIPE_REGISTRARS.add(new GoldIngotPurityUpgradeRecipe(purity));
        }
    }

    /**
     * 注册所有治炼炉配方
     */
    public static void registerAll(RecipeManager recipeManager) {
        for (RecipeRegistrar registrar : RECIPE_REGISTRARS) {
            registrar.register(recipeManager);
        }
    }

    private SmeltingFurnaceRecipes() {
        throw new UnsupportedOperationException("Utility class");
    }
}