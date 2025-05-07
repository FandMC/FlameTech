package cn.fandmc.structure.impl;

import cn.fandmc.structure.Structure;
import cn.fandmc.structure.StructureManager;
import cn.fandmc.util.LangUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class EnhancedWorkbenchStructure extends Structure {

    public EnhancedWorkbenchStructure() {
        super("enhanced_workbench", LangUtil.get("Block.EnhancedWorkbench.Name"));
    }

    @Override
    public boolean checkStructure(Location coreLocation) {
        Block workbench = coreLocation.getBlock();
        if (workbench.getType() != Material.CRAFTING_TABLE) return false;
        return coreLocation.clone().add(0, -1, 0).getBlock().getType() == Material.DISPENSER;
    }

    @Override
    public void onStructureCreated(Player player, Location coreLocation) {
        super.onStructureCreated(player, coreLocation);
        StructureManager.trackStructureLocation(coreLocation);
        player.sendMessage(LangUtil.get("BlockStructure.EnhancedWorkbench.Dispenser"));
    }
}
