package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.multiblock.base.BlockOffset;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * 位置相关工具类
 */
public final class LocationUtils {

    /**
     * 根据偏移量获取方块
     */
    public static Block getBlockAtOffset(Location center, BlockOffset offset) {
        if (!ValidationUtils.isValidLocation(center) || offset == null) {
            return null;
        }

        return center.clone().add(offset.getX(), offset.getY(), offset.getZ()).getBlock();
    }

    /**
     * 检查两个位置是否在同一个世界
     */
    public static boolean isSameWorld(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        }

        World world1 = loc1.getWorld();
        World world2 = loc2.getWorld();

        return world1 != null && world2 != null && world1.equals(world2);
    }

    /**
     * 计算两个位置之间的距离
     */
    public static double getDistance(Location loc1, Location loc2) {
        if (!isSameWorld(loc1, loc2)) {
            return Double.MAX_VALUE;
        }

        return loc1.distance(loc2);
    }

    /**
     * 检查位置是否在指定范围内
     */
    public static boolean isWithinRange(Location center, Location target, double range) {
        return getDistance(center, target) <= range;
    }

    /**
     * 获取位置的简短描述
     */
    public static String getLocationString(Location location) {
        if (!ValidationUtils.isValidLocation(location)) {
            return "Invalid Location";
        }

        return String.format("World: %s, X: %.1f, Y: %.1f, Z: %.1f",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ());
    }

    /**
     * 获取方块坐标的简短描述
     */
    public static String getBlockLocationString(Location location) {
        if (!ValidationUtils.isValidLocation(location)) {
            return "Invalid Location";
        }

        return String.format("World: %s, X: %d, Y: %d, Z: %d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    /**
     * 克隆位置（安全处理null）
     */
    public static Location cloneSafely(Location location) {
        return location != null ? location.clone() : null;
    }

    /**
     * 获取位置的中心点（方块中心）
     */
    public static Location getBlockCenter(Location location) {
        if (!ValidationUtils.isValidLocation(location)) {
            return location;
        }

        return new Location(
                location.getWorld(),
                location.getBlockX() + 0.5,
                location.getBlockY() + 0.5,
                location.getBlockZ() + 0.5
        );
    }

    /**
     * 检查位置是否为整数坐标
     */
    public static boolean isBlockLocation(Location location) {
        if (!ValidationUtils.isValidLocation(location)) {
            return false;
        }

        return location.getX() == location.getBlockX() &&
                location.getY() == location.getBlockY() &&
                location.getZ() == location.getBlockZ();
    }

    /**
     * 将位置转换为方块位置
     */
    public static Location toBlockLocation(Location location) {
        if (!ValidationUtils.isValidLocation(location)) {
            return location;
        }

        return new Location(
                location.getWorld(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    private LocationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}