package cn.fandmc.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class BookClickListener implements Listener {
    @EventHandler
    public void onBookRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (BookUtils.isGuideBook(item)) {
            event.setCancelled(true);
            String action = BookUtils.getBookAction(item);
            GUIManager.openGUI(player, action);
        }
    }
}
