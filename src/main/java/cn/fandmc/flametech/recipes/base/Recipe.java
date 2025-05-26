package cn.fandmc.flametech.recipes.base;

import org.bukkit.inventory.ItemStack;
import java.util.Map;

/**
 * 配方基类
 */
public abstract class Recipe {

    protected final String recipeId;
    protected final String displayName;
    protected final ItemStack result;
    protected final String multiblockId;
    protected final int unlockLevel;
    protected final RecipeType type;

    public Recipe(String recipeId, String displayName, ItemStack result,
                  String multiblockId, RecipeType type, int unlockLevel) {
        this.recipeId = recipeId;
        this.displayName = displayName;
        this.result = result != null ? result.clone() : null;
        this.multiblockId = multiblockId;
        this.type = type;
        this.unlockLevel = unlockLevel;
    }

    public Recipe(String recipeId, String displayName, ItemStack result,
                  String multiblockId, RecipeType type) {
        this(recipeId, displayName, result, multiblockId, type, 0);
    }

    /**
     * 检查输入是否匹配此配方
     */
    public abstract boolean matches(Map<Integer, ItemStack> inputs);

    /**
     * 获取配方所需的材料
     */
    public abstract Map<Integer, ItemStack> getIngredients();

    /**
     * 验证配方是否有效
     */
    public boolean isValid() {
        return recipeId != null && !recipeId.isEmpty() &&
                displayName != null && !displayName.isEmpty() &&
                result != null &&
                multiblockId != null && !multiblockId.isEmpty() &&
                type != null;
    }

    // Getter methods
    public String getRecipeId() { return recipeId; }
    public String getDisplayName() { return displayName; }
    public ItemStack getResult() { return result != null ? result.clone() : null; }
    public String getMultiblockId() { return multiblockId; }
    public int getUnlockLevel() { return unlockLevel; }
    public RecipeType getType() { return type; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recipe recipe = (Recipe) obj;
        return recipeId.equals(recipe.recipeId);
    }

    @Override
    public int hashCode() {
        return recipeId.hashCode();
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + recipeId + '\'' +
                ", name='" + displayName + '\'' +
                ", type=" + type +
                ", multiblock='" + multiblockId + '\'' +
                '}';
    }
}
