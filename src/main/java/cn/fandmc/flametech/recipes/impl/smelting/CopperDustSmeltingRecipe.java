package cn.fandmc.flametech.recipes.impl.smelting;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CopperDustSmeltingRecipe implements RecipeRegistrar {

    private static final String[] PATTERN = {
            "C"
    };

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        ItemStack copperDust = plugin.getMaterialManager().createMaterial("copper_dust").orElse(null);
        if (copperDust == null) return false;

        ItemStack result = plugin.getMaterialManager().createMaterial("copper_ingot_ft").orElse(null);
        if (result == null) return false;

        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('C', copperDust);

        ShapedRecipe recipe = new ShapedRecipe(
                "smelting_copper_dust",
                "铜粉熔炼",
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
        return "铜粉熔炼";
    }
}