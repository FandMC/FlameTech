package cn.fandmc.flametech.recipes.impl;

import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import cn.fandmc.flametech.recipes.impl.tool.*;
import cn.fandmc.flametech.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具配方管理器 - 负责注册所有工具配方
 */
public final class ToolRecipes {

    private static final List<RecipeRegistrar> RECIPE_REGISTRARS = new ArrayList<>();

    static {
        RECIPE_REGISTRARS.add(new ExplosivePickaxeRecipe());
        RECIPE_REGISTRARS.add(new SmeltingPickaxeRecipe());
        RECIPE_REGISTRARS.add(new MagnetRecipe());
    }

    /**
     * 注册所有工具配方
     */
    public static void registerAll(RecipeManager recipeManager) {
        for (RecipeRegistrar registrar : RECIPE_REGISTRARS) {
            registrar.register(recipeManager);
        }
    }

    private ToolRecipes() {
        throw new UnsupportedOperationException("Utility class");
    }
}