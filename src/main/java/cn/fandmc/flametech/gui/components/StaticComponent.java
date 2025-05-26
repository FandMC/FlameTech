package cn.fandmc.flametech.gui.components;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 静态组件 - 不可点击，只用于显示
 */
public class StaticComponent implements GUIComponent {

    private final ItemStack displayItem;

    public StaticComponent(ItemStack displayItem) {
        this.displayItem = displayItem != null ? displayItem.clone() : null;
    }

    @Override
    public ItemStack getDisplayItem() {
        return displayItem != null ? displayItem.clone() : null;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        // 静态组件不处理点击
    }

    @Override
    public boolean isClickable(Player player) {
        return false;
    }
}