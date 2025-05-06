package cn.fandmc.gui;

import cn.fandmc.Main;
import cn.fandmc.gui.guild.*;
import cn.fandmc.gui.item.BorderItem;
import cn.fandmc.listener.GUIListener;
import cn.fandmc.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class GUI {
    public static final Map<Integer, GUIComponent> components = new HashMap<>();
    private static final String GUI_NAME = getlang("GUI.Name");
    private static JavaPlugin plugin;
    private static final GUIHolder holder = new GUIHolder();
    public static final Map<Player, Stack<Map<Integer, GUIComponent>>> menuStack = new HashMap<>();

    public static void init(JavaPlugin plugin) {
        GUI.plugin = plugin;
        components.clear();
        registerListeners();
    }

    private static void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    private static void fillDefaultComponents() {
        for (int i = 0; i < 4; i++) {
            registerComponent(new BorderItem(i));
        }

        for (int i = 5; i < 9; i++) {
            registerComponent(new BorderItem(i));
        }

        for (int i = 45; i < 54; i++) {
            registerComponent(new BorderItem(i));
        }
    }

    public static Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(holder, 54, GUI_NAME);
        components.forEach((slot, comp) ->
                inv.setItem(slot, comp.createItem())
        );
        return inv;
    }

    public static void registerComponent(GUIComponent component) {
        components.put(component.getSlot(), component);
    }

    public static void open(Player player) {
        menuStack.computeIfAbsent(player, k -> new Stack<>())
                .push(new HashMap<>(components));

        cleanComponents();
        fillDefaultComponents();
        registerComponent(new BaseMachine());
        registerComponent(new StrangeToolTool());
        registerComponent(new PlayerHead(4, player));

        player.openInventory(createGUI());
        Logger.send(player, getlang("Gui.Open"));
    }

    public static void back(Player player) {
        if (menuStack.containsKey(player) && !menuStack.get(player).isEmpty()) {
            components.clear();
            components.putAll(menuStack.get(player).pop());
            player.openInventory(createGUI());
        }
    }

    public static Optional<GUIComponent> getComponentByItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Optional.empty();

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "flametech_item");
        Integer id = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

        if (id == null) return Optional.empty();

        return components.values().stream()
                .filter(comp -> comp.id() == id)
                .findFirst();
    }

    public static class GUIHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
    public static String getlang(String config){
        return Main.getconfig().color(config);
    }

    public static void cleanComponents() {
        components.clear();
    }
    public static Inventory createGUI(String title, int size) {
        Inventory inv = Bukkit.createInventory(holder, size, title);
        components.forEach((slot, comp) ->
                inv.setItem(slot, comp.createItem())
        );
        return inv;
    }
}
