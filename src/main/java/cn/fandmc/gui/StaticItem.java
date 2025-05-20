package cn.fandmc.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public record StaticItem(ItemStack item) implements GUIComponent {

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        // 默认不执行任何操作
    }
}
