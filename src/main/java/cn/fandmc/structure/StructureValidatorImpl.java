package cn.fandmc.structure;

import cn.fandmc.recipe.Recipe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class StructureValidatorImpl implements StructureValidator {
    @Override
    public boolean matchesStructure(@NotNull Location location, String requiredStructure) {
        if (requiredStructure == null) return true;
        return StructureManager.isValidStructureAt(location, requiredStructure);
    }
    @Override
    public void processStructureCraft(@NotNull Location structureLoc, Recipe recipe) {
        Block dispenserBlock = structureLoc.clone().add(0, -1, 0).getBlock();
        if (dispenserBlock.getType() != Material.DISPENSER) return;
        Dispenser dispenser = (Dispenser) dispenserBlock.getState();
        Inventory inv = dispenser.getInventory();

        if (recipe.matches(inv)) {
            recipe.ingredients.forEach((slot, reqItem) ->
                    inv.getItem(slot).setAmount(inv.getItem(slot).getAmount() - reqItem.getAmount())
            );
            inv.addItem(recipe.getResultPreview());
        }
    }
}