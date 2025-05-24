package cn.fandmc.gui;

import cn.fandmc.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {
    private static final Map<String, GUI> registeredGUIs = new HashMap<>();
    private static boolean initialized = false;

    public static void init(Main plugin) {
        if (!initialized) {
            plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new BookClickListener(), plugin);
            initialized = true;
        }
    }

    public static void registerGUI(GUI gui) {
        if (gui != null && gui.getName() != null) {
            if (!registeredGUIs.containsKey(gui.getName().toLowerCase())) {
                registeredGUIs.put(gui.getName().toLowerCase(), gui);
                gui.getPlugin().getLogger().info("已注册GUI: " + gui.getName());
            }
        }
    }

    public static void openGUI(Player player, String guiName) {
        if (player == null || guiName == null) {
            return;
        }

        GUI gui = registeredGUIs.get(guiName.toLowerCase());
        if (gui != null) {
            gui.open(player);
        } else {
            player.sendMessage("§c未找到GUI: " + guiName);
            player.sendMessage("§7可用的GUI: " + String.join(", ", registeredGUIs.keySet()));
        }
    }

    public static GUI getGUI(String name) {
        return registeredGUIs.get(name.toLowerCase());
    }

    public static boolean hasGUI(String name) {
        return registeredGUIs.containsKey(name.toLowerCase());
    }

    public static void unregisterGUI(String name) {
        registeredGUIs.remove(name.toLowerCase());
    }

    public static void clearAllGUIs() {
        registeredGUIs.clear();
    }
}