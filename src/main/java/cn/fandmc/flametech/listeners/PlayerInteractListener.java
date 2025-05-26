package cn.fandmc.flametech.listeners;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.manager.GUIManager;
import cn.fandmc.flametech.multiblock.manager.MultiblockManager;
import cn.fandmc.flametech.utils.BookUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * 玩家交互事件监听器
 */
public class PlayerInteractListener implements Listener {

    private final Main plugin;
    private final MultiblockManager multiblockManager;
    private final GUIManager guiManager;

    public PlayerInteractListener(Main plugin) {
        this.plugin = plugin;
        this.multiblockManager = plugin.getMultiblockManager();
        this.guiManager = plugin.getGuiManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        try {
            // 处理指南书点击
            if (handleGuideBookClick(event, player, item)) {
                return;
            }

            // 处理多方块结构交互
            if (handleMultiblockInteraction(event, player)) {
                return;
            }

        } catch (Exception e) {
            MessageUtils.logError("Error handling player interact event: " + e.getMessage());
            if (plugin.isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    private boolean handleGuideBookClick(PlayerInteractEvent event, Player player, ItemStack item) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        if (!BookUtils.isGuideBook(item)) {
            return false;
        }

        event.setCancelled(true);
        String action = BookUtils.getBookAction(item);
        guiManager.openGUI(player, action);
        return true;
    }

    private boolean handleMultiblockInteraction(PlayerInteractEvent event, Player player) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return false;
        }

        Location location = event.getClickedBlock().getLocation();
        return multiblockManager.handleInteraction(player, location, event);
    }
}
