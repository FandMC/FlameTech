package cn.fandmc.flametech.exceptions;

/**
 * 配方相关异常
 */
public class RecipeException extends FlameTechException {

    private final String recipeId;

    public RecipeException(String message, String recipeId) {
        super(message, "RECIPE_ERROR");
        this.recipeId = recipeId;
    }

    public RecipeException(String message, String recipeId, Throwable cause) {
        super(message, "RECIPE_ERROR", cause);
        this.recipeId = recipeId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    @Override
    public String toString() {
        return String.format("RecipeException[%s]: %s (Recipe: %s)",
                getErrorCode(), getMessage(), recipeId);
    }

    // 静态工厂方法
    public static RecipeException invalidRecipe(String recipeId) {
        return new RecipeException("Invalid recipe configuration", recipeId);
    }

    public static RecipeException recipeNotFound(String recipeId) {
        return new RecipeException("Recipe not found", recipeId);
    }

    public static RecipeException ingredientMismatch(String recipeId) {
        return new RecipeException("Recipe ingredients do not match", recipeId);
    }

    public static RecipeException registrationFailed(String recipeId, Throwable cause) {
        return new RecipeException("Failed to register recipe", recipeId, cause);
    }
}