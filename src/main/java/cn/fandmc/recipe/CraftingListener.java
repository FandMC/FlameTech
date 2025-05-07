package cn.fandmc.recipe;

import cn.fandmc.util.LangUtil;
import cn.fandmc.structure.StructureManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class CraftingListener implements Listener {

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (result == null) return;

        Recipe recipe = RecipeRegistry.getRecipeByResult(result);
        if (recipe == null) return;

        if (recipe.isStructureRecipe()) {
            return;
        }

        Location craftLocation = getCraftingLocation(event.getView());

        if (!StructureManager.isValidStructureAt(craftLocation, recipe.getRequiredStructureId())) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.sendMessage(LangUtil.get("Recipe.Error.MissingStructure"));
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
