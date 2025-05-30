package cn.fandmc.flametech.recipes.impl.smelting;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class IronDustSmeltingRecipe implements RecipeRegistrar {

    private static final String[] PATTERN = {
            "I"
    };

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        // 创建铁粉
        ItemStack ironDust = plugin.getMaterialManager().createMaterial("iron_dust").orElse(null);
        if (ironDust == null) return false;

        // 结果：原版铁锭
        ItemStack result = new ItemStack(Material.IRON_INGOT, 1);

        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('I', ironDust);

        ShapedRecipe recipe = new ShapedRecipe(
                "smelting_iron_dust",
                "铁粉熔炼",
                result,
                "smelting_furnace",
                PATTERN,
                ingredients,
                5
        );

        return recipeManager.registerRecipe(recipe);
    }

    @Override
    public String getRecipeName() {
        return "铁粉熔炼";
    }
}