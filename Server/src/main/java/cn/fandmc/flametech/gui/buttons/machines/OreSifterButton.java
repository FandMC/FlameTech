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
 * 筛矿机按钮
 */
public class OreSifterButton extends UnlockableComponent {

    public OreSifterButton() {
        super("multiblock.ore_sifter", "gui.basic_machines.ore_sifter.name");
    }

    @Override
    protected ItemStack createUnlockedDisplay(Player player) {
        return new ItemBuilder(Material.OAK_FENCE)
                .displayName(plugin.getConfigManager().getLang(getDisplayNameKey()))
                .lore(plugin.getConfigManager().getStringList("gui.basic_machines.ore_sifter.lore"))
                .build();
    }

    @Override
    protected void onAlreadyUnlocked(Player player, InventoryClickEvent event) {
        try {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    RecipeViewerGUI viewerGUI = RecipeViewerGUI.getInstance(plugin, "ore_sifter");
                    viewerGUI.open(player);
                } catch (Exception e) {
                    MessageUtils.logError("Failed to open ore sifter viewer: " + e.getMessage());
                    MessageUtils.sendMessage(player, "&c打开筛矿机查看器时发生错误");
                }
            });
        } catch (Exception e) {
            MessageUtils.logError("Error in OreSifterButton: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c操作失败，请稍后重试");
        }
    }
}