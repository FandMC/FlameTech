package cn.fandmc.structure.listener;

import cn.fandmc.recipe.Recipe;
import cn.fandmc.structure.*;
import cn.fandmc.structure.impl.EnhancedWorkbenchStructure;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class StructureCraftingListener implements Listener {
    private final StructureValidator validator;

    public StructureCraftingListener(StructureValidator validator) {
        this.validator = validator;
    }

    @EventHandler
    public void onStructureCraft(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null || clicked.getType() != Material.CRAFTING_TABLE) return;
        Structure structure = StructureManager.detectStructure(clicked.getLocation());
        if (structure instanceof EnhancedWorkbenchStructure workbench) {
            event.setCancelled(true);
            for (Recipe recipe : workbench.getRecipes()) {
                if (recipe.matches(event.getPlayer().getInventory())) {
                    validator.processStructureCraft(clicked.getLocation(), recipe);
                    event.getPlayer().sendMessage("§a结构配方合成成功！");
                    return;
                }
            }
            event.getPlayer().sendMessage("§c结构配方不匹配");
        }
    }

}

