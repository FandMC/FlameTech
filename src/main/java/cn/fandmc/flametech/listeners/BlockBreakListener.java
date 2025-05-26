package cn.fandmc.flametech.listeners;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.base.SpecialTool;
import cn.fandmc.flametech.items.manager.ItemManager;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * 方块破坏事件监听器
 */
public class BlockBreakListener implements Listener {

    private final Main plugin;
    private final ItemManager itemManager;

    public BlockBreakListener(Main plugin) {
        this.plugin = plugin;
        this.itemManager = plugin.getItemManager();
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

        // 基础验证
        if (!ValidationUtils.isValidPlayer(player) ||
                !ValidationUtils.isValidItem(tool) ||
                block == null) {
            return;
        }

        try {
            // 检查是否为特殊工具
            Optional<cn.fandmc.flametech.items.base.CustomItem> customItemOpt =
                    itemManager.getCustomItemFromStack(tool);

            if (customItemOpt.isPresent() && customItemOpt.get() instanceof SpecialTool specialTool) {
                handleSpecialToolBreak(event, player, block, tool, specialTool);
            }

        } catch (Exception e) {
            MessageUtils.logError("Error handling block break event: " + e.getMessage());
            if (plugin.isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    private void handleSpecialToolBreak(BlockBreakEvent event, Player player, Block block,
                                        ItemStack tool, SpecialTool specialTool) {
        if (!specialTool.canUse(player, block, tool)) {
            return;
        }

        if (specialTool.isOnCooldown(player)) {
            MessageUtils.sendMessage(player, "&c工具冷却中，请稍后再试！");
            return;
        }

        // 委托给特殊工具处理
        specialTool.handleBlockBreak(event, player, block, tool);
    }
}