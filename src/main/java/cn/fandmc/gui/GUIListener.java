package cn.fandmc.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof GUI.GUIHolder)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;

        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        Player player = (Player) e.getWhoClicked();
        GUI.getComponentByItem(item).ifPresent(component -> {
            component.onClick(player);
            player.updateInventory();
        });
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (isGUI(e)) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        GUI.guiHistory.remove(e.getPlayer());
    }
    private boolean isGUI(InventoryInteractEvent e) {
        return e.getInventory().getHolder() instanceof GUI.GUIHolder;
    }
}
