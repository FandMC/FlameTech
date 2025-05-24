package cn.fandmc.recipe;

public interface RecipeModule {
    void registerRecipes(RecipeManager manager);
    String getModuleName();
}