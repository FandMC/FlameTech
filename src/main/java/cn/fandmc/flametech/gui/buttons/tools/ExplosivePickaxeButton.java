// ExplosivePickaxeButton.java
package cn.fandmc.flametech.gui.buttons.tools;

import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.gui.components.UnlockableComponent;
import cn.fandmc.flametech.gui.impl.utils.ItemRecipeGUI;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.recipes.base.Recipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * 爆炸镐按钮
 */
public class ExplosivePickaxeButton extends UnlockableComponent {

    public ExplosivePickaxeButton() {
        super(ItemKeys.RECIPE_EXPLOSIVE_PICKAXE, "gui.tools.explosive_pickaxe.name");
    }

    @Override
    protected ItemStack createUnlockedDisplay(Player player) {
        return new ItemBuilder(Material.IRON_PICKAXE)
                .displayName(plugin.getConfigManager().getLang(getDisplayNameKey()))
                .lore(plugin.getConfigManager().getStringList("gui.tools.explosive_pickaxe.lore"))
                .build();
    }

    @Override
    protected void onAlreadyUnlocked(Player player, InventoryClickEvent event) {
        Optional<Recipe> recipeOpt = plugin.getRecipeManager().getRecipe(ItemKeys.ID_EXPLOSIVE_PICKAXE);
        ItemRecipeGUI recipeGUI = new ItemRecipeGUI(plugin, recipeOpt.get(), ItemKeys.ID_ENHANCED_CRAFTING_TABLE);
        recipeGUI.open(player);
    }
}