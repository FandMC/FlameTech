package cn.fandmc.gui.item.main;

import cn.fandmc.gui.templates.PaginatedGUI;
import cn.fandmc.gui.GUIComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainGUIItem implements GUIComponent {
    private final ItemStack item;

    public MainGUIItem() {
        item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a示例物品");
            item.setItemMeta(meta);
        }
    }

    @Override
    public ItemStack item() {
        return item;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        player.sendMessage("§b你点击了示例物品！");
    }
}
