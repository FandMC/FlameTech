package cn.fandmc.gui;

import cn.fandmc.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {
    static final Map<String, GUI> guiCache = new HashMap<>();

    public static void init(Main plugin) {
        GUIRegistry.init(); // 注册所有 GUI

        // 注册点击监听器
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    public static void openGUI(Player player, String guiName) {
        GUI gui = GUIRegistry.getGUI(guiName);
        if (gui != null) {
            gui.open(player);
        } else {
            player.sendMessage("§c未找到该界面");
        }
    }

    public static void handleItemClick(InventoryClickEvent event) {
        // 获取当前 GUI 实例并处理点击
        GUI gui = guiCache.get(event.getWhoClicked().getUniqueId());
        if (gui != null) {
            gui.handleItemClick(event);
        }
    }
}
