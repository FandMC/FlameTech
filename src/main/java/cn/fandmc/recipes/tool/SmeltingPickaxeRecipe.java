package cn.fandmc.recipes.tool;

import cn.fandmc.recipe.ShapedRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SmeltingPickaxeRecipe {

    public static ShapedRecipe create() {
        // 创建熔炼镐物品
        ItemStack smeltingPickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = smeltingPickaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l熔炼镐");
            meta.setLore(Arrays.asList(
                    "§7一把能够自动熔炼的镐子",
                    "§7挖掘矿物时自动熔炼成锭",
                    "§6节省熔炉燃料！",
                    "",
                    "§e[FlameTech 工具]"
            ));
            smeltingPickaxe.setItemMeta(meta);
        }

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
