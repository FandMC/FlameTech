package cn.fandmc.flametech.multiblock.base;

/**
 * 方块偏移量
 */
public class BlockOffset {

    private final int x;
    private final int y;
    private final int z;

    public BlockOffset(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BlockOffset that = (BlockOffset) obj;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return x * 31 * 31 + y * 31 + z;
    }

    @Override
    public String toString() {
        return String.format("BlockOffset{x=%d, y=%d, z=%d}", x, y, z);
    }

    /**
     * 创建零偏移量
     */
    public static BlockOffset zero() {
        return new BlockOffset(0, 0, 0);
    }

    /**
     * 添加偏移量
     */
    public BlockOffset add(int x, int y, int z) {
        return new BlockOffset(this.x + x, this.y + y, this.z + z);
    }

    /**
     * 添加另一个偏移量
     */
    public BlockOffset add(BlockOffset other) {
        return add(other.x, other.y, other.z);
    }

    /**
     * 减去偏移量
     */
    public BlockOffset subtract(int x, int y, int z) {
        return new BlockOffset(this.x - x, this.y - y, this.z - z);
    }

    /**
     * 减去另一个偏移量
     */
    public BlockOffset subtract(BlockOffset other) {
        return subtract(other.x, other.y, other.z);
    }

    /**
     * 计算到另一个偏移量的距离
     */
    public double distance(BlockOffset other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        int dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * 检查是否在指定范围内
     */
    public boolean isWithinRange(int range) {
        return Math.abs(x) <= range && Math.abs(y) <= range && Math.abs(z) <= range;
    }
}