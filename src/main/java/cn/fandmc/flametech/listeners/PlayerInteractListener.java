package cn.fandmc.flametech.listeners;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.gui.manager.GUIManager;
import cn.fandmc.flametech.items.base.CustomItem;
import cn.fandmc.flametech.items.tools.MagnetTool;
import cn.fandmc.flametech.multiblock.manager.MultiblockManager;
import cn.fandmc.flametech.utils.BookUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlayerInteractListener implements Listener {

    private final Main plugin;
    private final MultiblockManager multiblockManager;
    private final GUIManager guiManager;

    public PlayerInteractListener(Main plugin) {
        this.plugin = plugin;
        this.multiblockManager = plugin.getMultiblockManager();
        this.guiManager = plugin.getGuiManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        try {
            MessageUtils.logDebug("PlayerInteract: " + event.getAction() +
                    ", Item: " + (item != null ? item.getType() : "null") +
                    ", Player: " + player.getName());


            // 处理指南书点击
            if (handleGuideBookClick(event, player, item)) {
                return;
            }

            // 处理吸铁石右键点击
            if (handleMagnetClick(event, player, item)) {
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
        guiManager.openGUI(player, "main");
        return true;
    }

    /**
     * 处理吸铁石点击
     */
    private boolean handleMagnetClick(PlayerInteractEvent event, Player player, ItemStack item) {

        MessageUtils.logDebug("Checking magnet click for item: " +
                (item != null ? item.getType() : "null"));


        if (!plugin.getItemManager().isMagnet(item)) {
            return false;
        }

        MessageUtils.logDebug("Magnet detected! Processing click...");

        event.setCancelled(true);

        try {
            // 获取吸铁石工具实例并处理点击
            Optional<CustomItem> magnetOpt = plugin.getItemManager().getCustomItem(ItemKeys.ID_MAGNET);
            if (magnetOpt.isPresent() && magnetOpt.get() instanceof MagnetTool magnetTool) {
                // 调试信息
                if (plugin.isDebugMode()) {
                    MessageUtils.logInfo("Calling magnet handleRightClick...");
                }

                magnetTool.handleRightClick(event, player, item);
                return true;
            } else {
                MessageUtils.logError("Magnet tool not found or wrong type!");
                MessageUtils.sendMessage(player, "&c吸铁石工具未正确注册！");
                return false;
            }
        } catch (Exception e) {
            MessageUtils.logError("Error handling magnet click: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c使用吸铁石时发生错误: " + e.getMessage());
            return false;
        }
    }

    private boolean handleMultiblockInteraction(PlayerInteractEvent event, Player player) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return false;
        }

        Location location = event.getClickedBlock().getLocation();
        return multiblockManager.handleInteraction(player, location, event);
    }
}