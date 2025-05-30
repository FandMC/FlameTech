package cn.fandmc.flametech.recipes.impl.smelting;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GoldDustSmeltingRecipe implements RecipeRegistrar {

    private static final String[] PATTERN = {
            "G"
    };

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        ItemStack goldDust = plugin.getMaterialManager().createMaterial("gold_dust").orElse(null);
        if (goldDust == null) return false;

        ItemStack result = plugin.getMaterialManager().createMaterial("gold_ingot_10").orElse(null);
        if (result == null) return false;

        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('G', goldDust);

        ShapedRecipe recipe = new ShapedRecipe(
                "smelting_gold_dust",
                "金粉熔炼",
                result,
                "smelting_furnace",
                PATTERN,
                ingredients,
                8
        );

        return recipeManager.registerRecipe(recipe);
    }

    @Override
    public String getRecipeName() {
        return "金粉熔炼";
    }
}