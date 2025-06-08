package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * 书籍相关工具类
 */
public final class BookUtils {

    private static final NamespacedKey GUIDE_BOOK_KEY = new NamespacedKey(Main.getInstance(), ItemKeys.FLAME_TECH_GUIDE);

    /**
     * 创建指南书
     */
    public static ItemStack createGuideBook() {
        return createGuideBook("main");
    }

    /**
     * 创建指向特定GUI的指南书
     */
    public static ItemStack createGuideBook(String targetGUI) {
        Main plugin = Main.getInstance();

        String displayName = plugin.getConfigManager().getLang("guide_book.display_name");
        java.util.List<String> lore = plugin.getConfigManager().getStringList("guide_book.lore");

        return new ItemBuilder(Material.ENCHANTED_BOOK)
                .displayName(displayName)
                .lore(lore)
                .nbt(ItemKeys.FLAME_TECH_GUIDE, targetGUI)
                .glow()
                .build();
    }

    /**
     * 检查物品是否为指南书
     */
    public static boolean isGuideBook(ItemStack item) {
        if (ItemUtils.isAirOrNull(item) || item.getType() != Material.ENCHANTED_BOOK) {
            return false;
        }

        return ItemUtils.hasCustomNBT(item, ItemKeys.FLAME_TECH_GUIDE);
    }

    private BookUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}