// GUIListener.java
package cn.fandmc.listener;

import cn.fandmc.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        if (item == null) return;
        if (!(e.getInventory().getHolder() instanceof GUI.GUIHolder)) return;
        if (e.getAction() == InventoryAction.NOTHING) return;
        if (e.getHotbarButton() != -1) e.setCancelled(true);
        e.setCancelled(true);
        e.setResult(Event.Result.DENY);
        GUI.getComponentByItem(item).ifPresent(comp -> {
            comp.onClick(p);
            p.updateInventory();
        });
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (isGUI(e)) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    private boolean isGUI(InventoryInteractEvent e) {
        return e.getInventory().getHolder() instanceof GUI.GUIHolder;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        GUI.menuStack.remove(e.getPlayer());
    }
}
