package cn.fandmc.gui.item.tools;

import cn.fandmc.Main;
import cn.fandmc.gui.components.UnlockableButton;
import cn.fandmc.gui.impl.ItemRecipeGUI;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SmeltingPickaxeButton extends UnlockableButton {
    private final String recipeId = "smelting_pickaxe";

    public SmeltingPickaxeButton() {
        super("recipe.smelting_pickaxe", "gui.tools.smelting_pickaxe.name");
    }

    @Override
    protected ItemStack createUnlockedDisplay() {
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Main plugin = Main.getInstance();
            meta.setDisplayName(plugin.getConfigManager().getLang(getDisplayNameKey()));
            meta.setLore(plugin.getConfigManager().getStringList("gui.tools.smelting_pickaxe.lore"));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    protected void onAlreadyUnlocked(Player player, InventoryClickEvent event) {
        // 已解锁时打开配方详情界面
        Recipe recipe = RecipeManager.getInstance().getRecipe(recipeId);
        if (recipe != null) {
            ItemRecipeGUI recipeGUI = new ItemRecipeGUI(Main.getInstance(), recipe, "enhanced_crafting_table");
            recipeGUI.open(player);
        }
    }
}