package cn.fandmc.flametech.tools;

import cn.fandmc.flametech.Main;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ToolListener implements Listener {
    private final Main plugin;
    private final ToolManager toolManager;

    public ToolListener(Main plugin) {
        this.plugin = plugin;
        this.toolManager = ToolManager.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        // 如果事件已被取消，不处理
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();

        // 基础安全检查
        if (player == null || tool == null || block == null) {
            return;
        }

        try {
            // 检查是否为特殊工具并且功能已启用
            if (toolManager.isExplosivePickaxe(tool) && toolManager.isExplosivePickaxeEnabled()) {
                toolManager.getExplosivePickaxe().handleBlockBreak(event, player, block, tool);
            } else if (toolManager.isSmeltingPickaxe(tool) && toolManager.isSmeltingPickaxeEnabled()) {
                toolManager.getSmeltingPickaxe().handleBlockBreak(event, player, block, tool);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("处理工具事件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
