package cn.fandmc.gui.item.StrangeTool;

import cn.fandmc.Main;
import cn.fandmc.config.Config;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class SmeltingPickaxe implements GUIComponent {
    private static final Map<Material, Material> smeltingMap = Map.ofEntries(
            Map.entry(Material.IRON_ORE, Material.IRON_INGOT),
            Map.entry(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT),
            Map.entry(Material.RAW_IRON, Material.IRON_INGOT),
            Map.entry(Material.GOLD_ORE, Material.GOLD_INGOT),
            Map.entry(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT),
            Map.entry(Material.RAW_GOLD, Material.GOLD_INGOT),
            Map.entry(Material.COPPER_ORE, Material.COPPER_INGOT),
            Map.entry(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT),
            Map.entry(Material.RAW_COPPER, Material.COPPER_INGOT),
            Map.entry(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP)
    );


    @Override
    public int getSlot() {
        return 22;
    }

    @Override
    public ItemStack getItem() {
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();

        meta.setDisplayName(Config.ITEM_SMELTINGPICKAXE_NAME);
        meta.setLore(List.of(
                Config.ITEM_SMELTINGPICKAXE_LORE1,
                Config.ITEM_SMELTINGPICKAXE_LORE2
        ));

        pickaxe.setItemMeta(meta);
        return pickaxe;
    }

    @Override
    public void onClick(Player player) {
        //player.getInventory().addItem(createSmeltingPickaxe());
        GUI.refresh(player);
    }

    @Override
    public String id() {
        return "smelting_pickaxe";
    }

    public static ItemStack createSmeltingPickaxe() {
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();
        NamespacedKey typeKey = new NamespacedKey(Main.getPlugin(), "flametech_item");
        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, new SmeltingPickaxe().id());

        pickaxe.setItemMeta(meta);
        return pickaxe;
    }


    public static boolean isSmeltingPickaxe(ItemStack item) {
        if (item == null || item.getType() != Material.DIAMOND_PICKAXE) return false;

        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "flametech_item");
        String id = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return id != null && id == new SmeltingPickaxe().id();
    }


    public static Material getSmeltedResult(Material source) {
        return smeltingMap.getOrDefault(source, source);
    }
}
