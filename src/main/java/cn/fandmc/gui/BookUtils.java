package cn.fandmc.gui;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.ChatColor;
import org.bukkit.inventory.EquipmentSlot;
import cn.fandmc.Main;

import java.util.ArrayList;
import java.util.List;

public class BookUtils {
    private static final NamespacedKey GUIDE_BOOK_KEY = new NamespacedKey(Main.getInstance(), "flame_tech_guide");

    public static ItemStack createGuideBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta == null) return book;

        String displayName = Main.getInstance().getConfigManager().getLang("guide_book.display_name");
        List<String> lore = Main.getInstance().getConfigManager().getStringList("guide_book.lore");

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(
                GUIDE_BOOK_KEY,
                PersistentDataType.STRING,
                "main"
        );
        book.setItemMeta(meta);
        return book;
    }


    public static boolean isGuideBook(ItemStack item) {
        if (item == null || item.getType() != Material.ENCHANTED_BOOK) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(GUIDE_BOOK_KEY, PersistentDataType.STRING);
    }

    public static String getBookAction(ItemStack book) {
        ItemMeta meta = book.getItemMeta();
        if (meta == null) return "main";

        PersistentDataContainer container = meta.getPersistentDataContainer();
        String action = container.get(GUIDE_BOOK_KEY, PersistentDataType.STRING);

        return action != null ? action : "main";
    }
}