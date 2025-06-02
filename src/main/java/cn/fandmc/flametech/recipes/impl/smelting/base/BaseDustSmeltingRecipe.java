package cn.fandmc.flametech.recipes.impl.smelting.base;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.recipes.registrar.RecipeRegistrar;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 粉末熔炼配方基类
 */
public abstract class BaseDustSmeltingRecipe implements RecipeRegistrar {

    protected final String dustMaterialId;
    protected final String ingotMaterialId;
    protected final String recipeId;
    protected final String recipeName;
    protected final int requiredLevel;

    private static final String[] PATTERN = {
            "D"  // D = Dust
    };

    public BaseDustSmeltingRecipe(String dustMaterialId, String ingotMaterialId,
                                  String recipeId, String recipeName, int requiredLevel) {
        this.dustMaterialId = dustMaterialId;
        this.ingotMaterialId = ingotMaterialId;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public boolean register(RecipeManager recipeManager) {
        Main plugin = Main.getInstance();

        // 获取粉末材料
        ItemStack dustItem = plugin.getMaterialManager().createMaterial(dustMaterialId).orElse(null);
        if (dustItem == null) {
            plugin.getLogger().warning("无法创建材料: " + dustMaterialId);
            return false;
        }

        // 获取锭材料
        ItemStack ingotItem = plugin.getMaterialManager().createMaterial(ingotMaterialId).orElse(null);
        if (ingotItem == null) {
            plugin.getLogger().warning("无法创建材料: " + ingotMaterialId);
            return false;
        }

        // 创建材料映射
        Map<Character, ItemStack> ingredients = new HashMap<>();
        ingredients.put('D', dustItem);

        // 创建配方
        ShapedRecipe recipe = new ShapedRecipe(
                recipeId,
                recipeName,
                ingotItem,
                "smelting_furnace",
                PATTERN,
                ingredients,
                requiredLevel
        );

        return recipeManager.registerRecipe(recipe);
    }

    @Override
    public String getRecipeName() {
        return recipeName;
    }
}