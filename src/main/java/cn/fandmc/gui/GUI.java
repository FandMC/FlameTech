package cn.fandmc.gui;

import cn.fandmc.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class GUI implements org.bukkit.inventory.InventoryHolder {
    protected final String name;
    protected final String title;
    protected final int size;
    protected final Main plugin;
    protected Inventory inventory;
    protected final Map<Integer, GUIComponent> components = new HashMap<>();

    public GUI(Main plugin, String name, int size, String title) {
        this.plugin = plugin;
        this.name = name;
        this.size = size;
        this.title = title;
        this.inventory = Bukkit.createInventory(this, size, title);
        GUIRegistry.register(name, size, title);
    }

    public void setComponent(int slot, GUIComponent component) {
        if (slot >= 0 && slot < size) {
            components.put(slot, component);
            inventory.setItem(slot, component.item());
        }
    }

    public void removeComponent(int slot) {
        components.remove(slot);
        inventory.clear(slot);
    }

    public void clearComponents() {
        components.clear();
        inventory.clear();
    }

    public void refresh() {
        inventory.clear();
        for (Map.Entry<Integer, GUIComponent> entry : components.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().item());
        }
    }

    public void open(Player player) {
        if (player == null) return;
        buildGUI();
        refresh();
        player.openInventory(inventory);
    }

    protected abstract void buildGUI();

    public void onItemClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        GUIComponent component = components.get(slot);
        if (component != null) {
            component.onClick((Player) event.getWhoClicked(), event);
        }
    }

    public String getName() { return name; }
    public String getTitle() { return title; }
    public int getSize() { return size; }
    public Main getPlugin() { return plugin; }

    @Override
    public Inventory getInventory() { return inventory; }
}