package cn.fandmc.gui.item.tools;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.impl.ItemRecipeGUI;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeManager;
import cn.fandmc.unlock.UnlockManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class ExplosivePickaxeButton implements GUIComponent {
    private final String recipeId = "explosive_pickaxe";
    private final String unlockId = "recipe." + recipeId;

    @Override
    public ItemStack item() {
        return createLockedDisplay();
    }

    private ItemStack createLockedDisplay() {
        ItemStack locked = new ItemStack(Material.BARRIER);
        ItemMeta meta = locked.getItemMeta();
        if (meta != null) {
            Main plugin = Main.getInstance();
            meta.setDisplayName("§c" + plugin.getConfigManager().getLang("gui.tools.explosive_pickaxe.name") + " §7(未解锁)");

            List<String> lore = new ArrayList<>();
            lore.add("§7需要经验等级: §e" + UnlockManager.getInstance().getRequiredExp(unlockId));
            lore.add("");
            lore.add("§e点击解锁");
            meta.setLore(lore);
            locked.setItemMeta(meta);
        }
        return locked;
    }

    private ItemStack createUnlockedDisplay() {
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Main plugin = Main.getInstance();
            meta.setDisplayName(plugin.getConfigManager().getLang("gui.tools.explosive_pickaxe.name"));
            meta.setLore(plugin.getConfigManager().getStringList("gui.tools.explosive_pickaxe.lore"));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if (!UnlockManager.getInstance().isUnlocked(player, unlockId)) {
            UnlockManager.UnlockResult result = UnlockManager.getInstance().unlock(player, unlockId);

            if (result.isSuccess()) {
                player.sendMessage(Main.getInstance().getConfigManager().getLang("unlock.success")
                        .replace("%item%", Main.getInstance().getConfigManager().getLang("gui.tools.explosive_pickaxe.name")));
                event.getInventory().setItem(event.getSlot(), createUnlockedDisplay());
            } else {
                switch (result.getMessage()) {
                    case "insufficient_exp":
                        player.sendMessage(Main.getInstance().getConfigManager().getLang("unlock.insufficient_exp")
                                .replace("%required%", String.valueOf(result.getRequiredExp())));
                        break;
                    default:
                        player.sendMessage("§c解锁失败: " + result.getMessage());
                        break;
                }
            }
        } else {
            event.getInventory().setItem(event.getSlot(), createUnlockedDisplay());
            Recipe recipe = RecipeManager.getInstance().getRecipe(recipeId);
            if (recipe != null) {
                ItemRecipeGUI recipeGUI = new ItemRecipeGUI(Main.getInstance(), recipe, "enhanced_crafting_table");
                recipeGUI.open(player);
            }
        }
    }

    public ItemStack getItemForPlayer(Player player) {
        if (UnlockManager.getInstance().isUnlocked(player, unlockId)) {
            return createUnlockedDisplay();
        } else {
            return createLockedDisplay();
        }
    }
}