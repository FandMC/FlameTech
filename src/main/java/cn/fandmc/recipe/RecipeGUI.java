package cn.fandmc.recipe;

import cn.fandmc.Main;
import cn.fandmc.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RecipeGUI implements Listener {
    private static final Set<Integer> EMPTY_SLOTS = new HashSet<>(Arrays.asList(
            2, 3, 4,
            11, 12, 13,     16,
            20, 21, 22
    ));

    public RecipeGUI(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void open(Player player, String recipeId) {
        Recipe recipe = RecipeRegistry.getRecipe(recipeId);
        if (recipe == null) {
            player.sendMessage(Config.RECIPE_ERROR_INVALID);
            return;
        }

        Inventory inv = Bukkit.createInventory(
                new RecipeInventoryHolder(recipeId),
                27,
                Config.RECIPE_TITLE + recipe.getDisplayName()
        );

        fillBackground(inv);

        recipe.setupRecipeDisplay(inv);
        player.openInventory(inv);
    }

    private static void fillBackground(Inventory inv) {
        ItemStack grayGlass = createGrayGlassPane();
        for (int i = 0; i < inv.getSize(); i++) {
            if (!EMPTY_SLOTS.contains(i)) {
                inv.setItem(i, grayGlass);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isRecipeGUI(event.getView().getTopInventory())) {
            event.setCancelled(true);

            if (event.getHotbarButton() != -1) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isRecipeGUI(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    private boolean isRecipeGUI(Inventory inventory) {
        return inventory != null &&
                inventory.getHolder() instanceof RecipeInventoryHolder;
    }

    private static ItemStack createGrayGlassPane() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        return glass;
    }

}
