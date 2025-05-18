package cn.fandmc.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import cn.fandmc.gui.item.main.MainGUIItem;

public class MainGUI extends GUI {
    public MainGUI() {
        super("§eFlameTech 主界面", 9 * 3);
    }

    @Override
    protected Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(null, size, title);

        // 添加示例物品
        MainGUIItem item = new MainGUIItem();
        inv.setItem(10, item.getItem());

        // 缓存用于后续处理点击
        GUIManager.guiCache.put(title, this);

        return inv;
    }

    @Override
    public void handleItemClick(InventoryClickEvent event) {
        // 可以在这里添加通用处理逻辑
    }
}
