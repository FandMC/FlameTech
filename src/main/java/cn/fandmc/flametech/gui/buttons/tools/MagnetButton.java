package cn.fandmc.flametech.gui.buttons.tools;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.gui.components.UnlockableComponent;
import cn.fandmc.flametech.gui.impl.ItemRecipeGUI;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * 吸铁石按钮
 */
public class MagnetButton extends UnlockableComponent {

    public MagnetButton() {
        super(ItemKeys.RECIPE_MAGNET, "gui.tools.magnet.name");
    }

    @Override
    protected ItemStack createUnlockedDisplay(Player player) {
        return new ItemBuilder(Material.COMPASS)
                .displayName(plugin.getConfigManager().getLang(getDisplayNameKey()))
                .lore(plugin.getConfigManager().getStringList("gui.tools.magnet.lore"))
                .glow()
                .build();
    }

    @Override
    protected void onAlreadyUnlocked(Player player, InventoryClickEvent event) {
        try {
            // 已解锁时打开配方详情界面
            Optional<Recipe> recipeOpt = plugin.getRecipeManager().getRecipe(ItemKeys.ID_MAGNET);
            if (recipeOpt.isPresent()) {
                // 延迟执行以避免在事件处理中直接操作GUI
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    try {
                        ItemRecipeGUI recipeGUI = new ItemRecipeGUI(plugin, recipeOpt.get(), ItemKeys.ID_ENHANCED_CRAFTING_TABLE);
                        recipeGUI.open(player);
                    } catch (Exception e) {
                        MessageUtils.logError("Failed to open magnet recipe GUI: " + e.getMessage());
                        MessageUtils.sendMessage(player, "&c打开配方详情时发生错误");
                    }
                });
            } else {
                MessageUtils.sendMessage(player, "&c配方不存在，请联系管理员");
            }
        } catch (Exception e) {
            MessageUtils.logError("Error opening magnet recipe: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c打开配方时发生错误");
        }
    }
}