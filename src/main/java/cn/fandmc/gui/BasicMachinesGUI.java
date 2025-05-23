package cn.fandmc.gui;

import cn.fandmc.Main;
import cn.fandmc.gui.templates.SimpleGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class BasicMachinesGUI extends SimpleGUI {

    public BasicMachinesGUI(Main plugin) {
        super(plugin, "basic_machines", 54, "§e基础机器");
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
    }

    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a← 返回主页");
            meta.setLore(Arrays.asList(
                    "§7点击返回主界面"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
}