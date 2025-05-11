package cn.fandmc.structure;

import cn.fandmc.recipe.Recipe;
import cn.fandmc.structure.impl.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.List;

public class StructureGUI implements InventoryHolder {
    public static void open(Player player, Structure structure) {
        Inventory inv = Bukkit.createInventory(new StructureGUI(), 54,
                "§6结构配方 - " + structure.getName());
        StructureGuideDisplay.showStructureGuide(inv, structure);
        if (StructureManager.detectStructure(player.getLocation()) instanceof EnhancedWorkbenchStructure realStructure) {
            StructureRecipeDisplay.showRecipes(inv, realStructure);
        } else {
            inv.setItem(16, createNotBuiltYetItem());
        }
        player.openInventory(inv);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
    private static ItemStack createNotBuiltYetItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c结构未建立");
        meta.setLore(List.of("§7请在工作台上方放置发射器", "§7即可激活结构配方"));
        item.setItemMeta(meta);
        return item;
    }
    public static class StructureRecipeDisplay {
        public static void showRecipes(Inventory inv, EnhancedWorkbenchStructure structure) {
            int slot = 10;
            for (Recipe recipe : structure.getRecipes()) {
                inv.setItem(slot++, recipe.getResultPreview());
            }
        }
    }
}


