package cn.fandmc.gui.item.StrangeTool;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.util.LangUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ExplosivePickaxe implements GUIComponent {

    @Override
    public int getSlot() {
        return 23;
    }

    @Override
    public ItemStack getItem() {
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();

        meta.setDisplayName(LangUtil.get("Item.ExplosivePickaxe.Name"));
        meta.setLore(List.of(
                LangUtil.get("Item.ExplosivePickaxe.Lore1"),
                LangUtil.get("Item.ExplosivePickaxe.Lore2")
        ));

        pickaxe.setItemMeta(meta);
        return pickaxe;
    }

    @Override
    public void onClick(Player player) {
        GUI.refresh(player);
    }

    @Override
    public int id() {
        return 6;
    }

    public static ItemStack createExplosivePickaxe() {
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();
        NamespacedKey typeKey = new NamespacedKey(Main.getPlugin(), "flametech_item");
        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.INTEGER, new SmeltingPickaxe().id());
        pickaxe.setItemMeta(meta);
        return pickaxe;
    }

    public static boolean isExplosivePickaxe(ItemStack item) {
        if (item == null || item.getType() != Material.DIAMOND_PICKAXE) return false;

        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "flametech_item");
        Integer id = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        return id != null && id == new ExplosivePickaxe().id();
    }
}
