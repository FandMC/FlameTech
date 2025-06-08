// 文件：.\java\cn\fandmc\flametech\recipes\impl\smelting\GoldIngotPurityUpgradeRecipe.java
package cn.fandmc.flametech.recipes.impl.smelting;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GoldIngotPurityUpgradeRecipe implements RecipeRegistrar {

    private final int currentPurity;
    private final int nextPurity;

    private static final String[] PATTERN = {
            "DI"
    };

    public GoldIngotPurityUpgradeRecipe(int currentPurity) {
        this.currentPurity = currentPurity;
        this.nextPurity = currentPurity + 10;
    }

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        // 金粉
        ItemStack goldDust = plugin.getMaterialManager().createMaterial("gold_dust").orElse(null);
        if (goldDust == null) return false;

        // 当前纯度金锭
        ItemStack currentIngot = plugin.getMaterialManager().createMaterial("gold_ingot_" + currentPurity).orElse(null);
        if (currentIngot == null) return false;

        // 下一纯度金锭
        ItemStack nextIngot = plugin.getMaterialManager().createMaterial("gold_ingot_" + nextPurity).orElse(null);
        if (nextIngot == null) return false;

        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('D', goldDust);
        ingredients.put('I', currentIngot);

        ShapedRecipe recipe = new ShapedRecipe(
                "gold_purity_" + currentPurity + "_to_" + nextPurity,
                "金锭纯度提升 (" + currentPurity + "% → " + nextPurity + "%)",
                nextIngot,
                "smelting_furnace",
                PATTERN,
                ingredients,
                10 + (currentPurity / 10) // 纯度越高，需要的等级越高
        );

        return recipeManager.registerRecipe(recipe);
    }

    @Override
    public String getRecipeName() {
        return "金锭纯度提升 " + currentPurity + "% → " + nextPurity + "%";
    }
}