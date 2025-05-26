package cn.fandmc.flametech.gui.buttons.main;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 基础机器按钮
 */
public class BasicMachinesButton implements GUIComponent {

    @Override
    public ItemStack getDisplayItem() {
        return new ItemBuilder(Material.FURNACE)
                .displayName("&6基础机器")
                .lore(
                        "&7查看所有基础机器的配方和信息",
                        "&7包含：熔炉、压缩机、粉碎机等",
                        "",
                        "&e点击查看基础机器列表",
                        "",
                        "&c[FlameTech]"
                )
                .build();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        Main.getInstance().getGuiManager().openGUI(player, "basic_machines");
    }
}