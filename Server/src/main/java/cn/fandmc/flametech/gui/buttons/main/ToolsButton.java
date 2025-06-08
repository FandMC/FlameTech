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
 * 工具按钮
 */
public class ToolsButton implements GUIComponent {

    @Override
    public ItemStack getDisplayItem() {
        Main plugin = Main.getInstance();

        return new ItemBuilder(Material.DIAMOND_PICKAXE)
                .displayName(plugin.getConfigManager().getLang(Messages.GUI_BUTTONS_TOOLS_NAME))
                .lore(plugin.getConfigManager().getStringList(Messages.GUI_BUTTONS_TOOLS_LORE))
                .build();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        Main.getInstance().getGuiManager().openGUI(player, "tools");
    }
}