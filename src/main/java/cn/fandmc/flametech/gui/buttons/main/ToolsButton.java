package cn.fandmc.flametech.gui.buttons.main;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 工具按钮
 */
public class ToolsButton implements GUIComponent {

    @Override
    public ItemStack getDisplayItem() {
        return new ItemBuilder(Material.DIAMOND_PICKAXE)
                .displayName("&b工具")
                .lore(
                        "&7查看所有特殊工具的配方",
                        "&7包含：爆炸镐、熔炼镐等",
                        "",
                        "&e点击查看工具列表",
                        "",
                        "&c[FlameTech]"
                )
                .build();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        Main.getInstance().getGuiManager().openGUI(player, "tools");
    }
}