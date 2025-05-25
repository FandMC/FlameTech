package cn.fandmc.recipes.tool;

import cn.fandmc.recipe.ShapedRecipe;
import cn.fandmc.tools.ToolManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SmeltingPickaxeRecipe {

    public static ShapedRecipe create() {
        // 使用ToolManager创建真正的熔炼镐物品
        ItemStack smeltingPickaxe = ToolManager.getInstance().createSmeltingPickaxe();

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

        return new ShapedRecipe(
                "smelting_pickaxe",
                "熔炼镐",
                smeltingPickaxe,
                "enhanced_crafting_table",
                pattern,
                ingredients,
                20 // 需要20级经验解锁
        );
    }
}