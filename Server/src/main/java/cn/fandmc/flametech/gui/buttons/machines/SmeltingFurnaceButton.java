package cn.fandmc.flametech.gui.buttons.machines;

import cn.fandmc.flametech.gui.components.UnlockableComponent;
import cn.fandmc.flametech.gui.impl.utils.RecipeViewerGUI;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 治炼炉按钮
 */
public class SmeltingFurnaceButton extends UnlockableComponent {

    public SmeltingFurnaceButton() {
        super("multiblock.smelting_furnace", "gui.basic_machines.smelting_furnace.name");
    }

    @Override
    protected ItemStack createUnlockedDisplay(Player player) {
        return new ItemBuilder(Material.NETHER_BRICK_FENCE)
                .displayName(plugin.getConfigManager().getLang(getDisplayNameKey()))
                .lore(plugin.getConfigManager().getStringList("gui.basic_machines.smelting_furnace.lore"))
                .build();
    }

    @Override
    protected void onAlreadyUnlocked(Player player, InventoryClickEvent event) {
        try {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    RecipeViewerGUI viewerGUI = RecipeViewerGUI.getInstance(plugin, "smelting_furnace");
                    viewerGUI.open(player);
                } catch (Exception e) {
                    MessageUtils.logError("Failed to open smelting furnace viewer: " + e.getMessage());
                    MessageUtils.sendMessage(player, "&c打开治炼炉查看器时发生错误");
                }
            });
        } catch (Exception e) {
            MessageUtils.logError("Error in SmeltingFurnaceButton: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c操作失败，请稍后重试");
        }
    }
}
