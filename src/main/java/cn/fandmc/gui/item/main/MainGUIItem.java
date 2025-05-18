package cn.fandmc.gui.item.main;

import cn.fandmc.gui.GUIComponent;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

public class MainGUIItem implements GUIComponent {
    private final ItemStack item;

    public MainGUIItem() {
        item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b主菜单");
        item.setItemMeta(meta);
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        player.sendMessage("§a你点击了主菜单物品！");
    }
}
