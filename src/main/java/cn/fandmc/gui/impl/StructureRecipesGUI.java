package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.templates.PaginatedGUI;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureRecipesGUI extends PaginatedGUI {
    private final String multiblockId;
    private static final Map<String, StructureRecipesGUI> instances = new HashMap<>();

    private StructureRecipesGUI(Main plugin, String multiblockId) {
        super(plugin, "structure_recipes_" + multiblockId,
                plugin.getConfigManager().getLang("gui.structure_recipes.title")
                        .replace("%machine%", plugin.getConfigManager().getLang("multiblock." + multiblockId + ".name")));
        this.multiblockId = multiblockId;
        loadRecipes();
    }

    public static StructureRecipesGUI getInstance(Main plugin, String multiblockId) {
        String key = multiblockId;
        if (!instances.containsKey(key)) {
            StructureRecipesGUI gui = new StructureRecipesGUI(plugin, multiblockId);
            instances.put(key, gui);
            GUIManager.registerGUI(gui);
        }
        return instances.get(key);
    }

    private void loadRecipes() {
        List<Recipe> recipes = RecipeManager.getInstance().getRecipesForMultiblock(multiblockId);

        if (recipes.isEmpty()) {
            addPageItem(new NoRecipeItem());
        } else {
            for (Recipe recipe : recipes) {
                addPageItem(new RecipeItem(recipe));
            }
        }
    }

    @Override
    protected void setupControlButtons() {
        super.setupControlButtons();

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
                RecipeViewerGUI viewerGUI = RecipeViewerGUI.getInstance(plugin, multiblockId);
                viewerGUI.open(player);
            }
        });
    }

    private class NoRecipeItem implements GUIComponent {
        @Override
        public ItemStack item() {
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.getConfigManager().getLang("gui.structure_recipes.no_recipes"));
                meta.setLore(plugin.getConfigManager().getStringList("gui.structure_recipes.no_recipes_lore"));
                item.setItemMeta(meta);
            }
            return item;
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
        }
    }

    private class RecipeItem implements GUIComponent {
        private final Recipe recipe;

        public RecipeItem(Recipe recipe) {
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
                lore.add(plugin.getConfigManager().getLang("gui.structure_recipes.click_view_recipe"));

                meta.setLore(lore);
                display.setItemMeta(meta);
            }
            return display;
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            ItemRecipeGUI recipeGUI = new ItemRecipeGUI(plugin, recipe, multiblockId);
            recipeGUI.open(player);
        }
    }
}