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
 * 压力机按钮
 */
public class PressureMachineButton extends UnlockableComponent {

    public PressureMachineButton() {
        super("multiblock.pressure_machine", "gui.basic_machines.pressure_machine.name");
    }

    @Override
    protected ItemStack createUnlockedDisplay(Player player) {
        return new ItemBuilder(Material.GLASS)
                .displayName(plugin.getConfigManager().getLang(getDisplayNameKey()))
                .lore(plugin.getConfigManager().getStringList("gui.basic_machines.pressure_machine.lore"))
                .build();
    }

    @Override
    protected void onAlreadyUnlocked(Player player, InventoryClickEvent event) {
        try {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    RecipeViewerGUI viewerGUI = RecipeViewerGUI.getInstance(plugin, "pressure_machine");
                    viewerGUI.open(player);
                } catch (Exception e) {
                    MessageUtils.logError("Failed to open pressure machine viewer: " + e.getMessage());
                    MessageUtils.sendMessage(player, "&c打开压力机查看器时发生错误");
                }
            });
        } catch (Exception e) {
            MessageUtils.logError("Error in PressureMachineButton: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c操作失败，请稍后重试");
        }
    }
}