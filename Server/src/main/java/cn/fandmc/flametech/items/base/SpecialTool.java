package cn.fandmc.flametech.items.base;

import cn.fandmc.flametech.Main;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 特殊工具基类
 */
public abstract class SpecialTool extends CustomItem {

    public SpecialTool(Main plugin, String itemId, String displayName) {
        super(plugin, itemId, displayName);
    }

    /**
     * 处理方块破坏事件
     */
    public abstract void handleBlockBreak(BlockBreakEvent event, Player player, Block block, ItemStack tool);

    /**
     * 检查工具是否可以使用
     */
    public boolean canUse(Player player, Block block, ItemStack tool) {
        return isEnabled() && player != null && block != null && tool != null;
    }

    /**
     * 获取工具的冷却时间（毫秒）
     */
    public long getCooldownTime() {
        return 0;
    }

    /**
     * 检查玩家是否在冷却中
     */
    public boolean isOnCooldown(Player player) {
        // 可以实现冷却系统
        return false;
    }
}