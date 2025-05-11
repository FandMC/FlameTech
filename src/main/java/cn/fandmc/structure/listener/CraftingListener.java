package cn.fandmc.structure.listener;

import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeRegistry;
import cn.fandmc.structure.StructureManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (result == null) return;

        Recipe recipe = RecipeRegistry.getRecipeByResult(result);
        if (recipe == null) return;

        if (recipe.isStructureRecipe()) {
            Location craftLocation = getCraftingLocation(event.getView());

            if (!StructureManager.isValidStructureAt(craftLocation, recipe.getRequiredStructureId())) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("Recipe.Error.MissingStructure");
                return;
            }
            processEnhancedCrafting(event, craftLocation);
        }
    }

    private void processEnhancedCrafting(CraftItemEvent event, Location structureLoc) {
        event.setCancelled(true);

        Block dispenserBlock = structureLoc.clone().add(0, -1, 0).getBlock();
        if (dispenserBlock.getType() != Material.DISPENSER) return;

        Dispenser dispenser = (Dispenser) dispenserBlock.getState();
        Inventory inv = dispenser.getInventory();
        Recipe recipe = RecipeRegistry.getRecipeByResult(event.getRecipe().getResult());
        if (recipe.matches(inv)) {
            recipe.ingredients.forEach((slot, reqItem) ->
                    inv.getItem(slot).setAmount(inv.getItem(slot).getAmount() - reqItem.getAmount())
            );

            inv.addItem(recipe.getResultPreview());
        }
    }

    private Location getCraftingLocation(InventoryView view) {
        if (view.getTopInventory().getHolder() instanceof BlockInventoryHolder) {
            BlockInventoryHolder holder = (BlockInventoryHolder) view.getTopInventory().getHolder();
            return holder.getBlock().getLocation();
        }
        return (view.getPlayer()).getLocation();
    }
}
