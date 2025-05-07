package cn.fandmc.gui;

import cn.fandmc.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public interface GUIComponent {
    int getSlot();
    ItemStack getItem();
    void onClick(Player player);
    int id();

    default String getPageId() {
        return GUIRegistry.getComponentPage(this.getClass());
    }

    default ItemStack createItem() {
        ItemStack item = getItem().clone();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
                new NamespacedKey(Main.getPlugin(), "flametech_item"),
                PersistentDataType.INTEGER,
                id()
        );
        item.setItemMeta(meta);
        return item;
    }
}
