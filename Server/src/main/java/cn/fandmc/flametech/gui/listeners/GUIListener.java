package cn.fandmc.flametech.gui.listeners;

import cn.fandmc.flametech.gui.base.BaseGUI;
import cn.fandmc.flametech.gui.manager.GUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * GUI事件监听器
 */
public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGUI gui)) {
            return;
        }

        // 委托给GUI处理
        gui.handleClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (!(event.getInventory().getHolder() instanceof BaseGUI gui)) {
            return;
        }

        // 通知GUI管理器
        GUIManager.getInstance().closeGUI(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 清理玩家的GUI状态
        GUIManager.getInstance().handlePlayerDisconnect(event.getPlayer());
    }
}