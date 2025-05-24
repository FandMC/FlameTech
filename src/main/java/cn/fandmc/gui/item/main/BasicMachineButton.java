package cn.fandmc.gui.item.main;

import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.GUIManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class BasicMachineButton implements GUIComponent {
    private final ItemStack item;

    public BasicMachineButton() {
        this.item = createItem();
    }

    private ItemStack createItem() {
        ItemStack item = new ItemStack(Material.FURNACE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6基础机器");
            meta.setLore(Arrays.asList(
                    "§7查看所有基础机器的配方和信息",
                    "§7包含：熔炉、压缩机、粉碎机等",
                    "",
                    "§e点击查看基础机器列表",
                    "",
                    "§c[FlameTech]"
            ));
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
        GUIManager.openGUI(player, "basic_machines");
    }
}