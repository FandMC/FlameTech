package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.items.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * 简化的头颅工具类 - 只为特定材料使用
 */
public final class SkullUtils {

    // 预设玩家名（用于获取头颅）
    private static final String DEFAULT_SKULL_PLAYER = "20018";

    /**
     * 创建玩家头颅
     */
    public static ItemStack createPlayerSkull(String playerName, String displayName, List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            // 设置头颅所有者
            try {
                OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(playerName);
                meta.setOwningPlayer(player);
            } catch (Exception e) {
                // 如果设置失败，使用默认玩家
                try {
                    OfflinePlayer defaultPlayer = org.bukkit.Bukkit.getOfflinePlayer(DEFAULT_SKULL_PLAYER);
                    meta.setOwningPlayer(defaultPlayer);
                } catch (Exception fallbackError) {
                    MessageUtils.logWarning("Failed to set skull owner, using default");
                }
            }

            // 设置显示名称
            if (displayName != null) {
                meta.setDisplayName(MessageUtils.colorize(displayName));
            }

            // 设置lore
            if (lore != null) {
                meta.setLore(MessageUtils.colorize(lore));
            }

            skull.setItemMeta(meta);
        }

        return skull;
    }

    /**
     * 创建默认玩家头颅（使用预设玩家）
     */
    public static ItemStack createDefaultSkull(String displayName, List<String> lore) {
        return createPlayerSkull(DEFAULT_SKULL_PLAYER, displayName, lore);
    }

    /**
     * 通过ItemBuilder创建头颅
     */
    public static ItemBuilder createSkullBuilder(String playerName) {
        ItemStack skull = createPlayerSkull(playerName, null, null);
        return new ItemBuilder(skull);
    }

    /**
     * 检查是否为玩家头颅
     */
    public static boolean isPlayerHead(ItemStack item) {
        return item != null && item.getType() == Material.PLAYER_HEAD;
    }

    private SkullUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}