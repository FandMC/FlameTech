package cn.fandmc.gui;

import cn.fandmc.Main;
import cn.fandmc.gui.templates.PaginatedGUI;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeManager;
import cn.fandmc.recipe.ShapedRecipe;
import cn.fandmc.recipe.ShapelessRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeViewerGUI extends PaginatedGUI {
    private final String multiblockId;

    public RecipeViewerGUI(Main plugin, String multiblockId) {
        super(plugin, "recipe_viewer_" + multiblockId,
                plugin.getConfigManager().getLang("gui.recipe_viewer.title")
                        .replace("%machine%", multiblockId));
        this.multiblockId = multiblockId;
        loadRecipes();
    }

    private void loadRecipes() {
        List<Recipe> recipes = RecipeManager.getInstance().getRecipesForMultiblock(multiblockId);

        for (Recipe recipe : recipes) {
            addPageItem(new RecipeButton(recipe));
        }
    }

    @Override
    protected void setupControlButtons() {
        super.setupControlButtons();

        // 添加返回按钮
        setComponent(45, new GUIComponent() {
            @Override
            public ItemStack item() {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(plugin.getConfigManager().getLang("gui.common.back"));
                    item.setItemMeta(meta);
                }
                return item;
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                player.closeInventory();
            }
        });
    }

    // 配方按钮
    private class RecipeButton implements GUIComponent {
        private final Recipe recipe;

        public RecipeButton(Recipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public ItemStack item() {
            ItemStack display = recipe.getResult().clone();
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<>();

                lore.add("");
                lore.add(plugin.getConfigManager().getLang("gui.recipe_viewer.recipe_type")
                        .replace("%type%", recipe.getType().getDisplayName()));
                lore.add("");
                lore.add(plugin.getConfigManager().getLang("gui.recipe_viewer.click_to_view"));

                meta.setLore(lore);
                display.setItemMeta(meta);
            }
            return display;
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            // 打开配方详情GUI
            RecipeDetailGUI detailGUI = new RecipeDetailGUI(plugin, recipe);
            detailGUI.open(player);
        }
    }
}