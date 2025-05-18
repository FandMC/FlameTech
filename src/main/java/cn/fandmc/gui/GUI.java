package cn.fandmc.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class GUI {
    protected Inventory inventory;
    protected String title;
    protected int size;

    public GUI(String title, int size) {
        this.title = title;
        this.size = size;
        this.inventory = createInventory();
    }

    protected abstract Inventory createInventory();

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleItemClick(InventoryClickEvent event) {
        // 子类实现点击处理逻辑
    }
}
