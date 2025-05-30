package cn.fandmc.flametech.recipes.impl.smelting;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SilverDustSmeltingRecipe implements RecipeRegistrar {

    private static final String[] PATTERN = {
            "S"
    };

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        ItemStack silverDust = plugin.getMaterialManager().createMaterial("silver_dust").orElse(null);
        if (silverDust == null) return false;

        ItemStack result = plugin.getMaterialManager().createMaterial("silver_ingot").orElse(null);
        if (result == null) return false;

        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('S', silverDust);

        ShapedRecipe recipe = new ShapedRecipe(
                "smelting_silver_dust",
                "银粉熔炼",
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
        return "银粉熔炼";
    }
}
