package cn.fandmc.flametech.multiblock.base;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.utils.LocationUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 多方块结构基类
 */
public abstract class MultiblockStructure {

    protected final Main plugin;
    protected final String structureId;
    protected final String displayName;
    protected final Map<BlockOffset, Material> structure;
    protected final int unlockLevel;

    public MultiblockStructure(Main plugin, String structureId, String displayName,
                               Map<BlockOffset, Material> structure, int unlockLevel) {
        this.plugin = plugin;
        this.structureId = structureId;
        this.displayName = displayName;
        this.structure = structure != null ? new HashMap<>(structure) : new HashMap<>();
        this.unlockLevel = unlockLevel;

        validateStructure();
    }

    public MultiblockStructure(Main plugin, String structureId, String displayName,
                               Map<BlockOffset, Material> structure) {
        this(plugin, structureId, displayName, structure, 0);
    }

    private void validateStructure() {
        if (structure.isEmpty()) {
            throw new IllegalArgumentException("Multiblock structure cannot be empty");
        }

        // 检查是否有主方块（坐标原点）
        if (!structure.containsKey(BlockOffset.zero())) {
            MessageUtils.logWarning("Multiblock structure " + structureId + " has no main block at origin");
        }
    }

    /**
     * 检查指定位置是否符合结构
     */
    public boolean checkStructure(Location centerLocation) {
        if (!ValidationUtils.isValidLocation(centerLocation)) {
            return false;
        }

        try {
            for (Map.Entry<BlockOffset, Material> entry : structure.entrySet()) {
                BlockOffset offset = entry.getKey();
                Material requiredMaterial = entry.getValue();

                Block block = LocationUtils.getBlockAtOffset(centerLocation, offset);
                if (block == null || block.getType() != requiredMaterial) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            MessageUtils.logError("Error checking multiblock structure: " + e.getMessage());
            return false;
        }
    }

    /**
     * 激活结构时调用
     */
    public abstract void onActivate(Player player, Location location, PlayerInteractEvent event);

    /**
     * 检查结构是否可以制作指定配方
     */
    public boolean canCraft(String recipeId) {
        return true; // 默认实现，子类可以重写
    }

    /**
     * 检查玩家是否可以使用此结构
     */
    public boolean canUse(Player player) {
        if (!ValidationUtils.isValidPlayer(player)) {
            return false;
        }

        // 检查解锁状态
        String unlockId = "multiblock." + structureId;
        return plugin.getUnlockManager().isUnlocked(player, unlockId);
    }

    /**
     * 获取结构的边界框大小
     */
    public BoundingBox getBoundingBox() {
        if (structure.isEmpty()) {
            return new BoundingBox(0, 0, 0, 0, 0, 0);
        }

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockOffset offset : structure.keySet()) {
            minX = Math.min(minX, offset.getX());
            maxX = Math.max(maxX, offset.getX());
            minY = Math.min(minY, offset.getY());
            maxY = Math.max(maxY, offset.getY());
            minZ = Math.min(minZ, offset.getZ());
            maxZ = Math.max(maxZ, offset.getZ());
        }

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * 获取结构中的方块数量
     */
    public int getBlockCount() {
        return structure.size();
    }

    /**
     * 检查结构是否包含指定材料
     */
    public boolean containsMaterial(Material material) {
        return structure.containsValue(material);
    }

    /**
     * 获取结构的描述信息
     */
    public Map<String, Object> getStructureInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("id", structureId);
        info.put("display_name", displayName);
        info.put("block_count", getBlockCount());
        info.put("unlock_level", unlockLevel);

        BoundingBox bbox = getBoundingBox();
        info.put("dimensions", String.format("%dx%dx%d",
                bbox.getWidth(), bbox.getHeight(), bbox.getDepth()));

        return info;
    }

    // Getter methods
    public String getStructureId() { return structureId; }
    public String getDisplayName() { return displayName; }
    public Map<BlockOffset, Material> getStructure() { return new HashMap<>(structure); }
    public int getUnlockLevel() { return unlockLevel; }
    public Main getPlugin() { return plugin; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        MultiblockStructure that = (MultiblockStructure) obj;
        return structureId.equals(that.structureId);
    }

    @Override
    public int hashCode() {
        return structureId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("MultiblockStructure{id='%s', name='%s', blocks=%d}",
                structureId, displayName, getBlockCount());
    }

    /**
     * 边界框类
     */
    public static class BoundingBox {
        private final int minX, minY, minZ, maxX, maxY, maxZ;

        public BoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public int getWidth() { return maxX - minX + 1; }
        public int getHeight() { return maxY - minY + 1; }
        public int getDepth() { return maxZ - minZ + 1; }

        public int getMinX() { return minX; }
        public int getMinY() { return minY; }
        public int getMinZ() { return minZ; }
        public int getMaxX() { return maxX; }
        public int getMaxY() { return maxY; }
        public int getMaxZ() { return maxZ; }
    }
}