package cn.fandmc.flametech.gui.components;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 代表GUI中的一个组件
 */
public interface GUIComponent {

    /**
     * 获取显示的物品
     */
    ItemStack getDisplayItem();

    /**
     * 处理点击事件
     */
    void onClick(Player player, InventoryClickEvent event);

    /**
     * 检查组件是否可见
     */
    default boolean isVisible(Player player) {
        return true;
    }

    /**
     * 检查组件是否可点击
     */
    default boolean isClickable(Player player) {
        return true;
    }
}