package cn.fandmc.flametech.unlock;

/**
 * 可解锁内容接口
 * 所有需要解锁的内容（配方、多方块结构等）都应该实现此接口
 */
public interface Unlockable {

    /**
     * 获取解锁ID
     * @return 唯一的解锁标识符
     */
    String getUnlockId();

    /**
     * 获取解锁所需的经验等级
     * @return 经验等级，0表示不需要解锁
     */
    int getUnlockLevel();

    /**
     * 获取解锁类别
     * @return 类别名称（如 "recipe", "multiblock" 等）
     */
    String getUnlockCategory();

    /**
     * 获取显示名称
     * @return 用于显示的名称
     */
    String getDisplayName();

    /**
     * 获取解锁描述
     * @return 描述信息（可选）
     */
    default String getDescription() {
        return "";
    }

    /**
     * 是否需要解锁
     * @return 如果需要解锁返回true
     */
    default boolean requiresUnlock() {
        return getUnlockLevel() > 0;
    }
}