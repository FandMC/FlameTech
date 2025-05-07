package cn.fandmc.structure;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class StructureListener implements Listener {

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        if (event.getClickedBlock().getType() == Material.CRAFTING_TABLE) {
            Structure structure = StructureManager.detectStructure(event.getClickedBlock().getLocation());
            if (structure != null) {
                event.setCancelled(true);
                StructureManager.handleStructureCreation(event.getPlayer(), event.getClickedBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (type == Material.CRAFTING_TABLE || type == Material.DISPENSER) {
            StructureManager.handleStructureBreak(event.getBlock().getLocation());
        }
    }
}
