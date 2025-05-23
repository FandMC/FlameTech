package cn.fandmc.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUI gui)) {
            return;
        }

        event.setCancelled(true);

        if (event.getRawSlot() >= 0 && event.getRawSlot() < event.getInventory().getSize()) {
            gui.onItemClick(event);
        }
    }
}