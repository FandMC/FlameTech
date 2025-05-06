package cn.fandmc.structure.impl;

import cn.fandmc.Main;
import cn.fandmc.structure.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class EnhancedWorkbenchStructure extends Structure {

    public EnhancedWorkbenchStructure() {
        super("enhanced_workbench", "增强工作台");
    }

    @Override
    public boolean checkStructure(Location coreBlock) {
        Block workbench = coreBlock.getBlock();

        if (workbench.getType() != Material.CRAFTING_TABLE) return false;

        Block below = workbench.getRelative(0, -1, 0);
        return below.getType() == Material.DISPENSER;
    }

    @Override
    public void onStructureCreated(Player player, Location coreBlock) {
        super.onStructureCreated(player, coreBlock);
        player.sendMessage(getlang("BlockStructure.EnhancedWorkbench.Dispenser"));
        player.closeInventory();
    }

    public static String getlang(String config){
        return Main.getconfig().color(config);
    }
}
