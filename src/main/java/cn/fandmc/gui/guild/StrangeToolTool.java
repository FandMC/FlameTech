package cn.fandmc.gui.guild;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.gui.StrangeToolGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class StrangeToolTool implements GUIComponent {
    @Override
    public int getSlot() { return 11; }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getlang("Item.StrangeTool.Name"));
        meta.setLore(List.of(getlang("GUI.Tooltip.ClickToOpen")));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(Player player) {
        StrangeToolGUI.open(player);
    }

    @Override
    public int id() { return 1; }

    public static String getlang(String config){
        return Main.getconfig().color(config);
    }
}
