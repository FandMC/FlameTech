package cn.fandmc.gui.item.main;

import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.GUIManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class ToolsButton implements GUIComponent {
    private final ItemStack item;

    public ToolsButton() {
        this.item = createItem();
    }

    private ItemStack createItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b工具");
            meta.setLore(Arrays.asList(
                    "§7查看所有特殊工具的配方",
                    "§7包含：爆炸镐、熔炼镐等",
                    "",
                    "§e点击查看工具列表",
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
        GUIManager.openGUI(player, "tools");
    }
}