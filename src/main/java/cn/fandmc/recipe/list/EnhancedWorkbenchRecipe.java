package cn.fandmc.recipe.list;

import cn.fandmc.recipe.Recipe;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class EnhancedWorkbenchRecipe extends Recipe {
    private static final int WORKBENCH_SLOT = 12;
    private static final int DISPENSER_SLOT = 21;
    private static final int RESULT_SLOT = 16;

    public EnhancedWorkbenchRecipe() {
        super("enhanced_workbench", "增强工作台");
    }

    @Override
    public ItemStack getResultPreview() {
        return createEnhancedWorkbenchItem();
    }

    @Override
    public void setupRecipeDisplay(Inventory inv) {
        inv.setItem(WORKBENCH_SLOT, new ItemStack(Material.CRAFTING_TABLE));
        inv.setItem(DISPENSER_SLOT, new ItemStack(Material.DISPENSER));

        inv.setItem(RESULT_SLOT, getResultPreview());
    }

    private ItemStack createEnhancedWorkbenchItem() {
        ItemStack result = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName("§6增强工作台");
        meta.setLore(Arrays.asList(
                "§7由工作台和发射器合成",
                "§7提供更强大的合成功能"
        ));
        result.setItemMeta(meta);
        return result;
    }
}
