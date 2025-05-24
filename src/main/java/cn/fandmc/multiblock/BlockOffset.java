package cn.fandmc.multiblock;

public class BlockOffset {
    public final int x, y, z;

    public BlockOffset(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockOffset)) return false;
        BlockOffset other = (BlockOffset) obj;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return x * 31 * 31 + y * 31 + z;
    }
}