package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.item.machines.EnhancedCraftingTableButton;
import cn.fandmc.gui.templates.SimpleGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BasicMachinesGUI extends SimpleGUI {

    public BasicMachinesGUI(Main plugin) {
        super(plugin, "basic_machines", 54, plugin.getConfigManager().getLang("gui.basic_machines.title"));
        GUIManager.registerGUI(this);
    }

    @Override
    protected void buildGUI() {
        setBorder(Material.GRAY_STAINED_GLASS_PANE);

        setComponent(45, new GUIComponent() {
            @Override
            public ItemStack item() {
                return createBackButton();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                GUIManager.openGUI(player, "main");
            }
        });

        addBasicMachines();
    }

    private void addBasicMachines() {
        setCenterItem(new EnhancedCraftingTableButton());
    }

    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().getLang("gui.common.back"));
            item.setItemMeta(meta);
        }
        return item;
    }
}