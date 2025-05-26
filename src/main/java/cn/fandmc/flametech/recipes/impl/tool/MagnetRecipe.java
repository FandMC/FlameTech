package cn.fandmc.flametech.recipes.impl.tool;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 吸铁石配方注册器
 */
public class MagnetRecipe implements RecipeRegistrar {

    private static final String[] PATTERN = {
            "IMI",
            "RCR",
            "IMI"
    };

    private static final int UNLOCK_LEVEL = 10;

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        // 创建吸铁石物品
        ItemStack magnet = plugin.getItemManager().createMagnet();

        // 获取配方显示名称
        String displayName = plugin.getConfigManager().getLang(Messages.RECIPES_MAGNET_NAME);

        // 定义材料映射
        Map<Character, ItemStack> ingredients = createIngredients();

        // 创建配方
        ShapedRecipe recipe = new ShapedRecipe(
                ItemKeys.ID_MAGNET,
                displayName,
                magnet,
                ItemKeys.ID_ENHANCED_CRAFTING_TABLE,
                PATTERN,
                ingredients,
                UNLOCK_LEVEL
        );

        return recipeManager.registerRecipe(recipe);
    }

    @Override
    public String getRecipeName() {
        try {
            return Main.getInstance().getConfigManager().getLang(Messages.RECIPES_MAGNET_NAME);
        } catch (Exception e) {
            return "吸铁石"; // 默认名称
        }
    }

    /**
     * 创建配方材料映射
     * I = 铁锭, M = 磁石, R = 红石, C = 指南针
     */
    private Map<Character, ItemStack> createIngredients() {
        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('I', new ItemStack(Material.IRON_INGOT, 1));
        ingredients.put('M', new ItemStack(Material.LODESTONE, 1));
        ingredients.put('R', new ItemStack(Material.REDSTONE, 1));
        ingredients.put('C', new ItemStack(Material.COMPASS, 1));
        return ingredients;
    }
}