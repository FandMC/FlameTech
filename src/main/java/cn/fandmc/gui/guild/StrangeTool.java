package cn.fandmc.gui.guild;

import cn.fandmc.config.Config;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class StrangeTool implements GUIComponent {
    @Override
    public int getSlot() { return 11; }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Config.ITEM_STRANGETOOL_NAME);
        meta.setLore(List.of(Config.GUI_TOOLTIP_CLICKTOOPEN));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(Player player) {
        GUI.open(player,"strange_tool");
    }

    @Override
    public String id() { return "strange_Tool"; }

}
