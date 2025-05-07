package cn.fandmc.gui.guild;

import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.util.LangUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class BaseMachine implements GUIComponent {
    @Override
    public int getSlot() { return 10; }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(LangUtil.get("Item.BaseMachine.Name"));
        meta.setLore(List.of(LangUtil.get("GUI.Tooltip.ClickToOpen")));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(Player player) {
        GUI.open(player, "base_machine");
    }

    @Override
    public int id() { return 2; }

}
