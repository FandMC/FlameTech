package cn.fandmc.flametech.recipes.impl.smelting;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TinDustSmeltingRecipe implements RecipeRegistrar {

    private static final String[] PATTERN = {
            "T"
    };

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        ItemStack tinDust = plugin.getMaterialManager().createMaterial("tin_dust").orElse(null);
        if (tinDust == null) return false;

        ItemStack result = plugin.getMaterialManager().createMaterial("tin_ingot").orElse(null);
        if (result == null) return false;

        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('M', tinDust);

        ShapedRecipe recipe = new ShapedRecipe(
                "smelting_tin_dust",
                "锡粉熔炼",
                result,
                "smelting_furnace",
                PATTERN,
                ingredients,
                6
        );

        return recipeManager.registerRecipe(recipe);
    }

    @Override
    public String getRecipeName() {
        return "锡粉熔炼";
    }
}
