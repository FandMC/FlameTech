package cn.fandmc.flametech.recipes.impl.tool;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 爆炸镐配方注册器
 */
public class ExplosivePickaxeRecipe implements RecipeRegistrar {

    private static final String[] PATTERN = {
            "ITI",
            " S ",
            " S "
    };

    private static final int UNLOCK_LEVEL = 15;

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        // 创建爆炸镐物品
        ItemStack explosivePickaxe = plugin.getItemManager().createExplosivePickaxe();

        // 获取配方显示名称
        String displayName = plugin.getConfigManager().getLang(Messages.RECIPES_EXPLOSIVE_PICKAXE_NAME);

        // 定义材料映射
        Map<Character, ItemStack> ingredients = createIngredients();

        // 创建配方
        ShapedRecipe recipe = new ShapedRecipe(
                ItemKeys.ID_EXPLOSIVE_PICKAXE,
                displayName,
                explosivePickaxe,
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
            return Main.getInstance().getConfigManager().getLang(Messages.RECIPES_EXPLOSIVE_PICKAXE_NAME);
        } catch (Exception e) {
            return "爆炸镐";
        }
    }

    /**
     * 创建配方材料映射
     */
    private Map<Character, ItemStack> createIngredients() {
        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('I', new ItemStack(Material.IRON_INGOT, 1));
        ingredients.put('T', new ItemStack(Material.TNT, 1));
        ingredients.put('S', new ItemStack(Material.STICK, 1));
        return ingredients;
    }

    /**
     * 获取配方图案
     */
    public static String[] getPattern() {
        return PATTERN.clone();
    }

    /**
     * 获取解锁等级
     */
    public static int getUnlockLevel() {
        return UNLOCK_LEVEL;
    }
}
