package cn.fandmc.flametech.recipes.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具配方实现
 */
public final class ToolRecipes {

    /**
     * 注册所有工具配方
     */
    public static void registerAll(RecipeManager recipeManager) {
        try {
            registerExplosivePickaxeRecipe(recipeManager);
            registerSmeltingPickaxeRecipe(recipeManager);

            MessageUtils.logInfo("Registered all tool recipes");

        } catch (Exception e) {
            MessageUtils.logError("Failed to register tool recipes: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 注册爆炸镐配方
     */
    private static void registerExplosivePickaxeRecipe(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        // 创建爆炸镐物品
        ItemStack explosivePickaxe = plugin.getItemManager().createExplosivePickaxe();

        // 定义配方图案
        String[] pattern = {
                "ITI",
                " S ",
                " S "
        };

        // 定义材料映射
        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('I', new ItemStack(Material.IRON_INGOT, 1));
        ingredients.put('T', new ItemStack(Material.TNT, 1));
        ingredients.put('S', new ItemStack(Material.STICK, 1));

        ShapedRecipe recipe = new ShapedRecipe(
                ItemKeys.ID_EXPLOSIVE_PICKAXE,
                "爆炸镐",
                explosivePickaxe,
                ItemKeys.ID_ENHANCED_CRAFTING_TABLE,
                pattern,
                ingredients,
                15 // 需要15级经验解锁
        );

        if (!recipeManager.registerRecipe(recipe)) {
            throw new RuntimeException("Failed to register explosive pickaxe recipe");
        }
    }

    /**
     * 注册熔炼镐配方
     */
    private static void registerSmeltingPickaxeRecipe(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        // 创建熔炼镐物品
        ItemStack smeltingPickaxe = plugin.getItemManager().createSmeltingPickaxe();

        // 定义配方图案
        String[] pattern = {
                "IFI",
                " S ",
                " S "
        };

        // 定义材料映射
        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('I', new ItemStack(Material.IRON_INGOT, 1));
        ingredients.put('F', new ItemStack(Material.FURNACE, 1));
        ingredients.put('S', new ItemStack(Material.STICK, 1));

        ShapedRecipe recipe = new ShapedRecipe(
                ItemKeys.ID_SMELTING_PICKAXE,
                "熔炼镐",
                smeltingPickaxe,
                ItemKeys.ID_ENHANCED_CRAFTING_TABLE,
                pattern,
                ingredients,
                20 // 需要20级经验解锁
        );

        if (!recipeManager.registerRecipe(recipe)) {
            throw new RuntimeException("Failed to register smelting pickaxe recipe");
        }
    }

    /**
     * 创建自定义配方的示例方法
     */
    public static ShapedRecipe createCustomToolRecipe(String recipeId, String displayName,
                                                      ItemStack result, String[] pattern,
                                                      Map<Character, ItemStack> ingredients,
                                                      int unlockLevel) {
        return new ShapedRecipe(
                recipeId,
                displayName,
                result,
                ItemKeys.ID_ENHANCED_CRAFTING_TABLE,
                pattern,
                ingredients,
                unlockLevel
        );
    }

    /**
     * 获取铁镐基础配方（用于其他工具配方参考）
     */
    public static Map<Character, ItemStack> getIronPickaxeBase() {
        Map<Character, ItemStack> base = new HashMap<>();
        base.put('I', new ItemStack(Material.IRON_INGOT, 1));
        base.put('S', new ItemStack(Material.STICK, 1));
        return base;
    }

    /**
     * 获取钻石镐基础配方（用于高级工具配方参考）
     */
    public static Map<Character, ItemStack> getDiamondPickaxeBase() {
        Map<Character, ItemStack> base = new HashMap<>();
        base.put('D', new ItemStack(Material.DIAMOND, 1));
        base.put('S', new ItemStack(Material.STICK, 1));
        return base;
    }

    /**
     * 验证配方材料的可用性
     */
    private static boolean validateRecipeIngredients(Map<Character, ItemStack> ingredients) {
        for (Map.Entry<Character, ItemStack> entry : ingredients.entrySet()) {
            ItemStack item = entry.getValue();
            if (item == null || item.getType() == Material.AIR) {
                MessageUtils.logWarning("Invalid ingredient for character '" + entry.getKey() + "'");
                return false;
            }
        }
        return true;
    }

    /**
     * 验证配方图案的有效性
     */
    private static boolean validateRecipePattern(String[] pattern) {
        if (pattern == null || pattern.length == 0) {
            MessageUtils.logWarning("Recipe pattern cannot be null or empty");
            return false;
        }

        if (pattern.length > 3) {
            MessageUtils.logWarning("Recipe pattern height cannot exceed 3");
            return false;
        }

        for (String row : pattern) {
            if (row.length() > 3) {
                MessageUtils.logWarning("Recipe pattern width cannot exceed 3");
                return false;
            }
        }

        return true;
    }

    private ToolRecipes() {
        throw new UnsupportedOperationException("Utility class");
    }
}