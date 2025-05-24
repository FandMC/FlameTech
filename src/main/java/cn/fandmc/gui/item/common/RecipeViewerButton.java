package cn.fandmc.gui.item.common;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.RecipeViewerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class RecipeViewerButton implements GUIComponent {
    private final String multiblockId;
    private final Main plugin;

    public RecipeViewerButton(String multiblockId) {
        this.multiblockId = multiblockId;
        this.plugin = Main.getInstance();
    }

    @Override
    public ItemStack item() {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().getLang("gui.recipe_viewer.button.name"));
            meta.setLore(plugin.getConfigManager().getStringList("gui.recipe_viewer.button.lore"));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        RecipeViewerGUI viewerGUI = new RecipeViewerGUI(plugin, multiblockId);
        viewerGUI.open(player);
    }
}