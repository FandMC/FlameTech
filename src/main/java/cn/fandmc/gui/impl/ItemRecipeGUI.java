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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemRecipeGUI extends GUI {
    private final Recipe recipe;
    private final String fromMultiblock;

    private static final int[] RECIPE_SLOTS = {5, 6, 7, 14, 15, 16, 23, 24, 25};
    private static final int TYPE_SLOT = 11;
    private static final int ACTION_SLOT = 8;
    private static final int BACK_SLOT = 18;

    public ItemRecipeGUI(Main plugin, Recipe recipe, String fromMultiblock) {
        super(plugin, "item_recipe_" + recipe.getId(), 27,
                plugin.getConfigManager().getLang("gui.item_recipe.title")
                        .replace("%item%", recipe.getDisplayName()));
        this.recipe = recipe;
        this.fromMultiblock = fromMultiblock;
    }

    @Override
    protected void buildGUI() {
        clearComponents();

        displayRecipe();

        ItemStack typeIndicator = new ItemStack(Material.IRON_INGOT);
        ItemMeta typeMeta = typeIndicator.getItemMeta();
        if (typeMeta != null) {
            typeMeta.setDisplayName(plugin.getConfigManager().getLang("gui.item_recipe.recipe_type"));
            typeMeta.setLore(plugin.getConfigManager().getStringList("gui.item_recipe.recipe_type_lore"));
            typeIndicator.setItemMeta(typeMeta);
        }
        setComponent(TYPE_SLOT, new StaticItem(typeIndicator));

        ItemStack resultItem = recipe.getResult().clone();
        ItemMeta resultMeta = resultItem.getItemMeta();
        if (resultMeta != null) {
            List<String> lore = resultMeta.getLore();
            if (lore == null) lore = new ArrayList<>();
            lore.add("");
            lore.add("§e合成结果");
            lore.add("§7数量: x" + resultItem.getAmount());
            resultMeta.setLore(lore);
            resultItem.setItemMeta(resultMeta);
        }
        setComponent(ACTION_SLOT, new StaticItem(resultItem));

        setupBackButton(BACK_SLOT);
    }

    private void displayRecipe() {
        if (recipe instanceof ShapedRecipe) {
            displayShapedRecipe((ShapedRecipe) recipe);
        } else if (recipe instanceof ShapelessRecipe) {
            displayShapelessRecipe((ShapelessRecipe) recipe);
        } else {
            displayGenericRecipe();
        }
    }

    private void displayShapedRecipe(ShapedRecipe shaped) {
        String[] pattern = shaped.getPattern();
        Map<Character, ItemStack> ingredients = shaped.getIngredientMap();

        for (int slot : RECIPE_SLOTS) {
            removeComponent(slot);
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = row * 3 + col;

                if (row < pattern.length && col < pattern[row].length()) {
                    char c = pattern[row].charAt(col);
                    if (c != ' ' && ingredients.containsKey(c)) {
                        ItemStack ingredient = ingredients.get(c).clone();
                        ItemMeta meta = ingredient.getItemMeta();
                        if (meta != null) {
                            List<String> lore = meta.getLore();
                            if (lore == null) lore = new ArrayList<>();
                            lore.add("");
                            lore.add("§7需要数量: x" + ingredient.getAmount());
                            meta.setLore(lore);
                            ingredient.setItemMeta(meta);
                        }
                        setComponent(RECIPE_SLOTS[slotIndex], new StaticItem(ingredient));
                    }
                }
            }
        }
    }

    private void displayShapelessRecipe(ShapelessRecipe shapeless) {
        List<ItemStack> ingredients = shapeless.getIngredientList();

        for (int slot : RECIPE_SLOTS) {
            removeComponent(slot);
        }

        int totalIngredients = ingredients.size();
        int startSlot = (9 - totalIngredients) / 2;

        for (int i = 0; i < totalIngredients && i < RECIPE_SLOTS.length; i++) {
            ItemStack ingredient = ingredients.get(i).clone();
            ItemMeta meta = ingredient.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<>();
                lore.add("");
                lore.add("§7需要数量: x" + ingredient.getAmount());
                lore.add("§7无序配方");
                meta.setLore(lore);
                ingredient.setItemMeta(meta);
            }

            int slot = startSlot + i;
            if (slot >= 0 && slot < RECIPE_SLOTS.length) {
                setComponent(RECIPE_SLOTS[slot], new StaticItem(ingredient));
            }
        }
    }

    private void displayGenericRecipe() {
        Map<Integer, ItemStack> ingredients = recipe.getIngredients();

        for (Map.Entry<Integer, ItemStack> entry : ingredients.entrySet()) {
            int slot = entry.getKey();
            ItemStack ingredient = entry.getValue().clone();

            if (slot < RECIPE_SLOTS.length) {
                ItemMeta meta = ingredient.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.getLore();
                    if (lore == null) lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7需要数量: x" + ingredient.getAmount());
                    meta.setLore(lore);
                    ingredient.setItemMeta(meta);
                }
                setComponent(RECIPE_SLOTS[slot], new StaticItem(ingredient));
            }
        }
    }
}