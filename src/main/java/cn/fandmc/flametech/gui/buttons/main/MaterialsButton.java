package cn.fandmc.flametech.gui.buttons.main;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 矿物按钮 - 主界面上的矿物/材料入口
 */
public class MaterialsButton implements GUIComponent {

    @Override
    public ItemStack getDisplayItem() {
        Main plugin = Main.getInstance();
        int totalMaterials = plugin.getMaterialManager().getRegisteredMaterialCount();

        List<String> lore = new ArrayList<>();
        lore.add("&7查看和获取各种工业材料");
        lore.add("&7包含：粉末、锭、合金、宝石等");
        lore.add("");
        lore.add("&e材料统计:");
        lore.add("&7总材料数: &e" + totalMaterials + " &7种");
        lore.add("");
        lore.add("&e点击进入材料大全");

        return new ItemBuilder(Material.RAW_COPPER)
                .displayName("&6&l矿物材料")
                .lore(lore)
                .glow()
                .build();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        Main.getInstance().getGuiManager().openGUI(player, "materials");
    }
}