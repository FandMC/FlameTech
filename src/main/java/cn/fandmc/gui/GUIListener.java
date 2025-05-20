package cn.fandmc.gui;

import cn.fandmc.gui.templates.PaginatedGUI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof GUI gui) {
            event.setCancelled(true);

            if (event.getRawSlot() >= 0 && event.getRawSlot() < event.getInventory().getSize()) {
                gui.onItemClick(event);
            }

            if (event.getRawSlot() == 38 && gui instanceof PaginatedGUI) {
                ((PaginatedGUI) gui).previousPage((Player) event.getWhoClicked());
            }

            if (event.getRawSlot() == 40 && gui instanceof PaginatedGUI) {
                ((PaginatedGUI) gui).nextPage((Player) event.getWhoClicked());
            }
        }
    }
}
