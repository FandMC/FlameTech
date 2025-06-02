package cn.fandmc.flametech.recipes.impl.smelting;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.impl.smelting.base.BaseDustSmeltingRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class IronDustSmeltingRecipe extends BaseDustSmeltingRecipe {

    public IronDustSmeltingRecipe() {
        super("iron_dust", null, "smelting_iron_dust", "铁粉熔炼", 5);
    }

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        ItemStack ironDust = plugin.getMaterialManager().createMaterial("iron_dust").orElse(null);
        if (ironDust == null) return false;

        ItemStack result = new ItemStack(Material.IRON_INGOT, 1);

        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('D', ironDust);

        cn.fandmc.flametech.recipes.base.ShapedRecipe recipe = new cn.fandmc.flametech.recipes.base.ShapedRecipe(
                recipeId,
                recipeName,
                result,
                "smelting_furnace",
                new String[]{"D"},
                ingredients,
                requiredLevel
        );

        return recipeManager.registerRecipe(recipe);
    }
}