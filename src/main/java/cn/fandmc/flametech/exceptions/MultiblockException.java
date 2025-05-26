package cn.fandmc.flametech.exceptions;

/**
 * 多方块结构相关异常
 */
public class MultiblockException extends FlameTechException {

    private final String structureId;

    public MultiblockException(String message, String structureId) {
        super(message, "MULTIBLOCK_ERROR");
        this.structureId = structureId;
    }

    public MultiblockException(String message, String structureId, Throwable cause) {
        super(message, "MULTIBLOCK_ERROR", cause);
        this.structureId = structureId;
    }

    public String getStructureId() {
        return structureId;
    }

    @Override
    public String toString() {
        return String.format("MultiblockException[%s]: %s (Structure: %s)",
                getErrorCode(), getMessage(), structureId);
    }

    // 静态工厂方法
    public static MultiblockException invalidStructure(String structureId) {
        return new MultiblockException("Invalid multiblock structure", structureId);
    }

    public static MultiblockException structureNotFound(String structureId) {
        return new MultiblockException("Multiblock structure not found", structureId);
    }

    public static MultiblockException structureMismatch(String structureId) {
        return new MultiblockException("Structure layout does not match", structureId);
    }

    public static MultiblockException activationFailed(String structureId, Throwable cause) {
        return new MultiblockException("Failed to activate multiblock structure", structureId, cause);
    }
}
