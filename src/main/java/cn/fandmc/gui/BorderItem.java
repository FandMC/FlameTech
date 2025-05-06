package cn.fandmc.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BorderItem implements GUIComponent {
    private final int slot;
    private final ItemStack item;

    public BorderItem(int slot) {
        this.slot = slot;
        this.item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);
    }

    @Override public int getSlot() { return slot; }
    @Override public ItemStack getItem() { return item.clone(); }
    @Override public void onClick(Player player) {}
    @Override public int id() { return 0; }
}
