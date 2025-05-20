package cn.fandmc.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface GUIComponent {
    ItemStack item();
    void onClick(Player player, InventoryClickEvent event);
}
