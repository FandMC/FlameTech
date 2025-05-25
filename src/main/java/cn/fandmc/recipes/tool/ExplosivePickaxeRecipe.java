package cn.fandmc.recipes.tool;

import cn.fandmc.recipe.ShapedRecipe;
import cn.fandmc.tools.ToolManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ExplosivePickaxeRecipe {

    public static ShapedRecipe create() {
        // 使用ToolManager创建真正的爆炸镐物品
        ItemStack explosivePickaxe = ToolManager.getInstance().createExplosivePickaxe();

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

        return new ShapedRecipe(
                "explosive_pickaxe",
                "爆炸镐",
                explosivePickaxe,
                "enhanced_crafting_table",
                pattern,
                ingredients,
                15 // 需要15级经验解锁
        );
    }
}