package cn.fandmc.gui.item.BaseMachine;

import cn.fandmc.Main;
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
        meta.setDisplayName(getlang("BlockStructure.EnhancedWorkbench.Title"));
        meta.setLore(List.of(
                getlang("GUI.Tooltip.ClickToCraft"),
                getlang("BlockStructure.EnhancedWorkbench.Description")
        ));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(Player player) {
        RecipeGUI.open(player, "enhanced_workbench");
    }

    @Override
    public int id() { return 4; }

    public static String getlang(String config){
        return Main.getconfig().color(config);
    }
}
