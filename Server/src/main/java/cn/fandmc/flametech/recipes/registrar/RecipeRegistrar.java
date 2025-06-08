package cn.fandmc.flametech.recipes.registrar;

import cn.fandmc.flametech.recipes.manager.RecipeManager;

/**
 * 配方注册器接口
 */
public interface RecipeRegistrar {

    /**
     * 注册配方到配方管理器
     * @param recipeManager 配方管理器
     * @return 是否注册成功
     */
    boolean register(RecipeManager recipeManager);

    /**
     * 获取配方名称（用于日志）
     * @return 配方名称
     */
    String getRecipeName();
}