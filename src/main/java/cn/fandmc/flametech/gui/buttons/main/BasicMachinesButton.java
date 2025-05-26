package cn.fandmc.flametech.gui.buttons.main;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
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
        Main plugin = Main.getInstance();

        return new ItemBuilder(Material.FURNACE)
                .displayName(plugin.getConfigManager().getLang(Messages.GUI_BUTTONS_BASIC_MACHINES_NAME))
                .lore(plugin.getConfigManager().getStringList(Messages.GUI_BUTTONS_BASIC_MACHINES_LORE))
                .build();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        Main.getInstance().getGuiManager().openGUI(player, "basic_machines");
    }
}