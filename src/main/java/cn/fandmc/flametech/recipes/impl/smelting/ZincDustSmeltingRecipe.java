package cn.fandmc.flametech.recipes.impl.smelting;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ZincDustSmeltingRecipe implements RecipeRegistrar {

    private static final String[] PATTERN = {
            "Z"
    };

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        ItemStack zincDust = plugin.getMaterialManager().createMaterial("zinc_dust").orElse(null);
        if (zincDust == null) return false;

        ItemStack result = plugin.getMaterialManager().createMaterial("zinc_ingot").orElse(null);
        if (result == null) return false;

        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('Z', zincDust);

        ShapedRecipe recipe = new ShapedRecipe(
                "smelting_zinc_dust",
                "锌粉熔炼",
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
        return "锌粉熔炼";
    }
}