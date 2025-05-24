package cn.fandmc.recipe;

import org.bukkit.inventory.ItemStack;
import java.util.Map;

public abstract class Recipe {
    protected final String id;
    protected final String displayName;
    protected final ItemStack result;
    protected final String multiblockId;
    protected final int unlockLevel; // 新增：解锁等级

    public Recipe(String id, String displayName, ItemStack result, String multiblockId, int unlockLevel) {
        this.id = id;
        this.displayName = displayName;
        this.result = result;
        this.multiblockId = multiblockId;
        this.unlockLevel = unlockLevel;
    }

    public Recipe(String id, String displayName, ItemStack result, String multiblockId) {
        this(id, displayName, result, multiblockId, 0);
    }

    public abstract boolean matches(Map<Integer, ItemStack> inputs);
    public abstract Map<Integer, ItemStack> getIngredients();
    public abstract RecipeType getType();

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public ItemStack getResult() { return result.clone(); }
    public String getMultiblockId() { return multiblockId; }
    public int getUnlockLevel() { return unlockLevel; }
}