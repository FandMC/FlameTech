package cn.fandmc.flametech.listeners.multiblock;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.multiblock.impl.OreWasher;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 洗矿机特殊交互监听器
 * 处理手持沙砾右键活版门的特殊逻辑
 */
public class OreWasherListener implements Listener {

    private final Main plugin;

    public OreWasherListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 只处理右键点击方块的情况
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        // 检查基础条件
        if (!ValidationUtils.isValidPlayer(player) ||
                clickedBlock == null ||
                clickedBlock.getType() != Material.OAK_TRAPDOOR ||
                handItem == null ||
                handItem.getType() != Material.GRAVEL) {
            return;
        }

        try {
            Location trapdoorLocation = clickedBlock.getLocation();

            // 检查是否是洗矿机结构
            if (isOreWasherStructure(trapdoorLocation)) {
                // 检查是否正在洗矿
                if (OreWasher.isWashing(trapdoorLocation)) {
                    event.setCancelled(true);
                    return;
                }

                // 检查炼药锅是否有水
                if (!hasCauldronWithWater(trapdoorLocation)) {
                    MessageUtils.sendLocalizedMessage(player, "multiblock.ore_washer.need_water");
                    event.setCancelled(true);
                    return;
                }

                // 让多方块管理器处理洗矿逻辑
                boolean handled = plugin.getMultiblockManager().handleInteraction(player, trapdoorLocation, event);
                if (handled) {
                    // 如果成功处理，确保事件被取消，防止活版门被打开
                    event.setCancelled(true);
                }
            }

        } catch (Exception e) {
            MessageUtils.logError("Error in OreWasherListener: " + e.getMessage());
        }
    }

    /**
     * 检查是否为洗矿机结构
     */
    private boolean isOreWasherStructure(Location trapdoorLocation) {
        // 检查下方是否有炼药锅
        Block belowBlock = trapdoorLocation.clone().add(0, -1, 0).getBlock();
        return belowBlock.getType() == Material.CAULDRON;
    }

    /**
     * 检查炼药锅是否有水
     */
    private boolean hasCauldronWithWater(Location trapdoorLocation) {
        Block cauldronBlock = trapdoorLocation.clone().add(0, -1, 0).getBlock();

        if (cauldronBlock.getType() != Material.CAULDRON) {
            return false;
        }

        // 检查炼药锅的水位
        // 在实际实现中，可以通过检查方块数据来确定水位
        org.bukkit.block.data.Levelled cauldronData = (org.bukkit.block.data.Levelled) cauldronBlock.getBlockData();
        return cauldronData.getLevel() > 0;
    }
}