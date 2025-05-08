package cn.fandmc.structure.impl;

import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeRegistry;
import cn.fandmc.structure.StructureManager;
import cn.fandmc.util.LangUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

public class EnhancedCraftingListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorkbenchInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block workbench = event.getClickedBlock();
        if (workbench == null || workbench.getType() != Material.CRAFTING_TABLE) return;

        Location coreLoc = workbench.getLocation();
        if (!StructureManager.isValidStructureAt(coreLoc, "enhanced_workbench")) return;

        Block dispenserBlock = coreLoc.clone().add(0, -1, 0).getBlock();
        if (dispenserBlock.getType() != Material.DISPENSER) return;

        event.setCancelled(true);
        processCrafting(event.getPlayer(), (Dispenser) dispenserBlock.getState());
    }

    private void processCrafting(Player player, Dispenser dispenser) {
        Inventory inv = dispenser.getInventory();

        Recipe matchedRecipe = null;
        for (Recipe recipe : RecipeRegistry.getAllRecipes()) {
            if (recipe.matches(inv)) {
                matchedRecipe = recipe;
                break;
            }
        }

        if (matchedRecipe == null) {
            player.sendMessage(LangUtil.get("Crafting.Error.InvalidRecipe"));
            return;
        }

        consumeIngredients(inv, matchedRecipe);
        ItemStack result = matchedRecipe.getResultPreview().clone();

        Map<Integer, ItemStack> remaining = inv.addItem(result);

        if (!remaining.isEmpty()) {
            remaining.values().forEach(item ->
                    dispenser.getWorld().dropItemNaturally(dispenser.getLocation(), item)
            );
            player.sendMessage(LangUtil.get("Crafting.Error.FullInventory"));
        } else {
            player.sendMessage(LangUtil.get("Crafting.Success"));
        }
    }

    private void consumeIngredients(Inventory inv, Recipe recipe) {
        for (Map.Entry<Integer, ItemStack> entry : recipe.ingredients.entrySet()) {
            int slot = entry.getKey();
            ItemStack required = entry.getValue();
            ItemStack current = inv.getItem(slot);

            if (current == null) continue;

            int newAmount = current.getAmount() - required.getAmount();
            if (newAmount > 0) {
                current.setAmount(newAmount);
            } else {
                inv.setItem(slot, null);
            }
        }
    }
}
