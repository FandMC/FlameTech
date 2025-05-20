package cn.fandmc.gui;

import cn.fandmc.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {
    private static final Map<String, GUI> guiMap = new HashMap<>();

    public static void registerGUI(GUI gui) {
        if (gui.getName() != null && !guiMap.containsKey(gui.getName().toLowerCase())) {
            guiMap.put(gui.getName().toLowerCase(), gui);
            gui.buildInventory();
        }
    }

    public static void init(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(new BookClickListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    public static void openGUI(Player player, String name) {
        GUI gui = guiMap.get(name.toLowerCase());
        if (gui != null) {
            gui.open(player);
        } else {
            player.sendMessage("§c未找到该界面");
        }
    }

    public static void handleItemClicked(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof GUI gui) {
            gui.onItemClick(event);
        }
    }
}
