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

    /**
     * 获取指南书的目标GUI
     */
    public static String getBookAction(ItemStack book) {
        if (!isGuideBook(book)) {
            return "main";
        }

        String action = ItemUtils.getCustomNBTValue(book, ItemKeys.FLAME_TECH_GUIDE);
        return action != null ? action : "main";
    }

    /**
     * 设置指南书的目标GUI
     */
    public static ItemStack setBookAction(ItemStack book, String action) {
        if (!isGuideBook(book)) {
            return book;
        }

        ItemStack result = book.clone();
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(GUIDE_BOOK_KEY, PersistentDataType.STRING, action);
            result.setItemMeta(meta);
        }

        return result;
    }

    /**
     * 创建配方书
     */
    public static ItemStack createRecipeBook(String recipeId) {
        return new ItemBuilder(Material.WRITTEN_BOOK)
                .displayName("&6配方: " + recipeId)
                .lore(
                        "&7这是一本配方指南",
                        "&7右键查看详细配方",
                        "",
                        "&e[FlameTech 配方]"
                )
                .nbt("recipe_book", recipeId)
                .build();
    }

    /**
     * 检查是否为配方书
     */
    public static boolean isRecipeBook(ItemStack item) {
        return !ItemUtils.isAirOrNull(item) &&
                item.getType() == Material.WRITTEN_BOOK &&
                ItemUtils.hasCustomNBT(item, "recipe_book");
    }

    /**
     * 获取配方书的配方ID
     */
    public static String getRecipeId(ItemStack recipeBook) {
        if (!isRecipeBook(recipeBook)) {
            return null;
        }

        return ItemUtils.getCustomNBTValue(recipeBook, "recipe_book");
    }

    private BookUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}