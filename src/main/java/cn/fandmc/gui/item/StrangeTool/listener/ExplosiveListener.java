package cn.fandmc.gui.item.StrangeTool.listener;

import cn.fandmc.gui.item.StrangeTool.ExplosivePickaxe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ExplosiveListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        Block block = event.getBlock();

        if (!ExplosivePickaxe.isExplosivePickaxe(tool)) return;

        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().createExplosion(loc, 3.0f, false, false);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block target = loc.clone().add(x, y, z).getBlock();
                    if (target.getType() != Material.BEDROCK) {
                        target.breakNaturally();
                    }
                }
            }
        }
    }
}
