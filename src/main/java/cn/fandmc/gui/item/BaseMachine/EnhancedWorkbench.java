package cn.fandmc.gui.item.BaseMachine;

import cn.fandmc.config.Config;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.recipe.RecipeGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
public class EnhancedWorkbench implements GUIComponent {
    @Override
    public int getSlot() { return 0; }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Config.BLOCKSTRUCTURE_ENHANCEDWORKBENCH_TITLE);
        meta.setLore(List.of(
                Config.GUI_TOOLTIP_CLICKTOCRAFT,
                Config.BLOCKSTRUCTURE_ENHANCEDWORKBENCH_DESCRIPTION
        ));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(Player player) {
        RecipeGUI.open(player, "enhanced_workbench");
    }

    @Override
    public String id() { return "enhanced_workbench"; }
}
