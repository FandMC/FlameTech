package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ValidationUtils {

    /**
     * 验证玩家是否有效
     */
    public static boolean isValidPlayer(Player player) {
        return player != null && player.isOnline() && player.isValid();
    }

    /**
     * 验证物品是否有效
     */
    public static boolean isValidItem(ItemStack item) {
        return item != null && !ItemUtils.isAirOrNull(item);
    }

    /**
     * 验证位置是否有效
     */
    public static boolean isValidLocation(Location location) {
        return location != null && location.getWorld() != null;
    }

    /**
     * 验证字符串是否有效（非空且非null）
     */
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * 验证数字是否在指定范围内
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * 验证数字是否在指定范围内
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * 安全获取整数配置值
     */
    public static int getConfigInt(String path, int defaultValue, int min, int max) {
        try {
            int value = Main.getInstance().getConfig().getInt(path, defaultValue);
            return Math.max(min, Math.min(max, value));
        } catch (Exception e) {
            MessageUtils.logWarning("Failed to read config value '" + path + "', using default: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * 安全获取布尔配置值
     */
    public static boolean getConfigBoolean(String path, boolean defaultValue) {
        try {
            return Main.getInstance().getConfig().getBoolean(path, defaultValue);
        } catch (Exception e) {
            MessageUtils.logWarning("Failed to read config value '" + path + "', using default: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * 检查玩家是否可以破坏指定距离内的方块
     */
    public static boolean canPlayerBreakAtDistance(Player player, Location blockLocation, double maxDistance) {
        if (!isValidPlayer(player) || !isValidLocation(blockLocation)) {
            return false;
        }

        Location playerLocation = player.getLocation();
        if (!playerLocation.getWorld().equals(blockLocation.getWorld())) {
            return false;
        }

        double distance = playerLocation.distance(blockLocation);
        return distance <= maxDistance;
    }

    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}