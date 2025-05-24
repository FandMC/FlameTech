package cn.fandmc.gui;

import cn.fandmc.Main;
import cn.fandmc.gui.item.machines.EnhancedCraftingTableButton;
import cn.fandmc.gui.templates.SimpleGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class BasicMachinesGUI extends SimpleGUI {

    public BasicMachinesGUI(Main plugin) {
        super(plugin, "basic_machines", 54, plugin.getConfigManager().getLang("gui.basic_machines.title"));
        GUIManager.registerGUI(this);
    }

    @Override
    protected void buildGUI() {
        setBorder(Material.GRAY_STAINED_GLASS_PANE);

        // 返回按钮
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

        // 添加基础机器
        addBasicMachines();
    }

    private void addBasicMachines() {
        // 增强型工作台
        setCenterItem(0, new EnhancedCraftingTableButton());

        // TODO: 添加其他基础机器
        // setCenterItem(1, new FurnaceButton());
        // setCenterItem(2, new CrusherButton());
        // setCenterItem(3, new CompressorButton());
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