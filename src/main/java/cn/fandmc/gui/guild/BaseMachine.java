package cn.fandmc.gui.guild;

import cn.fandmc.config.Config;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.structure.Structure;
import cn.fandmc.structure.StructureGUI;
import cn.fandmc.structure.StructureManager;
import cn.fandmc.structure.impl.EnhancedWorkbenchStructure;
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
        meta.setDisplayName(Config.ITEM_BASEMACHINE_NAME);
        meta.setLore(List.of(Config.GUI_TOOLTIP_CLICKTOOPEN));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(Player player) {
        StructureGUI.open(player, new EnhancedWorkbenchStructure());
    }

    @Override
    public String id() { return "base_machine"; }

}
