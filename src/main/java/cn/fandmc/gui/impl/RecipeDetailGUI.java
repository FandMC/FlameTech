package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.StaticItem;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.ShapedRecipe;
import cn.fandmc.recipe.ShapelessRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class RecipeDetailGUI extends GUI {
    private final Recipe recipe;

    public RecipeDetailGUI(Main plugin, Recipe recipe) {
        super(plugin, "recipe_detail_" + recipe.getId(), 54,
                plugin.getConfigManager().getLang("gui.recipe_detail.title")
                        .replace("%recipe%", recipe.getDisplayName()));
        this.recipe = recipe;
    }

    @Override
    protected void buildGUI() {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }

        for (int i = 0; i < 9; i++) {
            setComponent(i, new StaticItem(border));
            setComponent(45 + i, new StaticItem(border));
        }

        if (recipe instanceof ShapedRecipe) {
            displayShapedRecipe((ShapedRecipe) recipe);
        } else if (recipe instanceof ShapelessRecipe) {
            displayShapelessRecipe((ShapelessRecipe) recipe);
        }

        setComponent(24, new StaticItem(recipe.getResult()));

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta arrowMeta = arrow.getItemMeta();
        if (arrowMeta != null) {
            arrowMeta.setDisplayName(plugin.getConfigManager().getLang("gui.recipe_detail.crafting"));
            arrow.setItemMeta(arrowMeta);
        }
        setComponent(23, new StaticItem(arrow));

        setComponent(49, new GUIComponent() {
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
                RecipeViewerGUI viewerGUI = RecipeViewerGUI.getInstance(plugin, recipe.getMultiblockId());
                viewerGUI.open(player);
            }
        });
    }

    private void displayShapedRecipe(ShapedRecipe shapedRecipe) {
        String[] pattern = shapedRecipe.getPattern();
        Map<Character, ItemStack> ingredients = shapedRecipe.getIngredientMap();

        int[] slots = {
                10, 11, 12,
                19, 20, 21,
                28, 29, 30
        };

        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[row].length(); col++) {
                char c = pattern[row].charAt(col);
                if (c != ' ' && ingredients.containsKey(c)) {
                    int slot = slots[row * 3 + col];
                    setComponent(slot, new StaticItem(ingredients.get(c)));
                }
            }
        }
    }

    private void displayShapelessRecipe(ShapelessRecipe shapelessRecipe) {
        int[] slots = {
                10, 11, 12,
                19, 20, 21,
                28, 29, 30
        };

        int index = 0;
        for (ItemStack ingredient : shapelessRecipe.getIngredientList()) {
            if (index < slots.length) {
                setComponent(slots[index], new StaticItem(ingredient));
                index++;
            }
        }
    }
}