package cn.fandmc.gui.item.StrangeTool.listener.folia;

import cn.fandmc.Main;
import cn.fandmc.gui.item.StrangeTool.SmeltingPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class SmeltingListener implements Listener {
    private final Main plugin;

    public SmeltingListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        if (!SmeltingPickaxe.isSmeltingPickaxe(tool)) return;
        if (!Bukkit.isOwnedByCurrentRegion(block.getLocation())) return;
        Material result = SmeltingPickaxe.getSmeltedResult(block.getType());
        event.setDropItems(false);
        Bukkit.getServer().getRegionScheduler().execute(plugin, block.getLocation(), () -> dropItem(block, result));
    }

    private void dropItem(Block block, Material material) {
        Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);
        if (block.getWorld().isChunkLoaded(dropLocation.getBlockX() >> 4, dropLocation.getBlockZ() >> 4)) {
            block.getWorld().dropItemNaturally(dropLocation, new ItemStack(material));
        }
    }
}
