package cn.fandmc.gui.item.machines;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.RecipeViewerGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class EnhancedCraftingTableButton implements GUIComponent {
    private final ItemStack item;

    public EnhancedCraftingTableButton() {
        this.item = createItem();
    }

    private ItemStack createItem() {
        ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Main plugin = Main.getInstance();
            meta.setDisplayName(plugin.getConfigManager().getLang("gui.basic_machines.enhanced_crafting.name"));
            meta.setLore(plugin.getConfigManager().getStringList("gui.basic_machines.enhanced_crafting.lore"));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public ItemStack item() {
        return item.clone();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        // 打开配方查看器
        RecipeViewerGUI viewerGUI = new RecipeViewerGUI(Main.getInstance(), "enhanced_crafting_table");
        viewerGUI.open(player);
    }
}