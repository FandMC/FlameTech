package cn.fandmc.gui;

import cn.fandmc.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class GUI implements org.bukkit.inventory.InventoryHolder {
    private final String name;
    private final String title;
    protected final int size;
    protected final List<GUIComponent> guiItems = new ArrayList<>();
    private Inventory inventory;
    private final Main plugin;

    public GUI(Main plugin, String name, int size, String title) {
        this.plugin = plugin;
        this.name = name;
        this.size = size;
        this.title = title;
        GUIRegistry.register(name, size, title);
        buildItems();
    }

    protected abstract void buildItems();

    protected void buildInventory() {
        inventory = Bukkit.createInventory(this, size, title);
        int slot = 0;
        for (GUIComponent item : guiItems) {
            if (slot < size) {
                inventory.setItem(slot++, item.item());
            }
        }
    }

    public void open(Player player) {
        if (player == null) {
            return;
        }

        if (inventory == null) {
            buildInventory();
        }

        player.openInventory(inventory);
    }


    public void onItemClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot >= 0 && slot < guiItems.size()) {
            guiItems.get(slot).onClick((Player) event.getWhoClicked(), event);
        }
    }

    public void addItem(GUIComponent item) {
        guiItems.add(item);
    }

    public void addItem(int index, GUIComponent component) {
        if (index >= 0 && index < guiItems.size()) {
            guiItems.add(index, component);
        } else {
            guiItems.add(component);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
