package cn.fandmc.structure.impl;

import cn.fandmc.config.Config;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.impl.SmeltingPickaxeRecipe;
import cn.fandmc.structure.Structure;
import cn.fandmc.structure.StructureManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnhancedWorkbenchStructure extends Structure {
    private final Set<Recipe> workbenchRecipes = new HashSet<>();

    public EnhancedWorkbenchStructure() {
        super("enhanced_workbench", Config.BLOCK_ENHANCEDWORKBENCH_NAME);
        registerWorkbenchRecipes();
    }

    @Override
    public boolean checkStructure(Location coreLocation) {
        Block workbench = coreLocation.getBlock();
        if (workbench.getType() != Material.CRAFTING_TABLE) return false;
        Block dispenser = coreLocation.clone().add(0, -1, 0).getBlock();
        return dispenser.getType() == Material.DISPENSER;
    }

    private void registerWorkbenchRecipes() {
        workbenchRecipes.add(new SmeltingPickaxeRecipe());
    }

    public Set<Recipe> getRecipes() {
        return workbenchRecipes;
    }

    @Override
    public void onStructureCreated(Player player, Location coreLocation) {
        super.onStructureCreated(player,coreLocation);
        StructureManager.trackStructureLocation(coreLocation);
        player.sendMessage(Config.BLOCKSTRUCTURE_ENHANCEDWORKBENCH_DISPENSER);
    }

    @Override
    public List<int[]> getStructureLayout() {
        return Arrays.asList(
                new int[]{0, 12}, // 工作台在槽位12
                new int[]{1, 21}  // 发射器在下方
        );
    }
}
