package cn.fandmc.recipes.tool;

import cn.fandmc.recipe.ShapedRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExplosivePickaxeRecipe {

    public static ShapedRecipe create() {
        // 创建爆炸镐物品
        ItemStack explosivePickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = explosivePickaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c§l爆炸镐");
            meta.setLore(Arrays.asList(
                    "§7一把充满破坏力的镐子",
                    "§7能够炸毁大片区域的方块",
                    "§c小心使用！",
                    "",
                    "§e[FlameTech 工具]"
            ));
            explosivePickaxe.setItemMeta(meta);
        }

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
