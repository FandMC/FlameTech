package cn.fandmc.flametech.gui.buttons.machines;

import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.gui.components.UnlockableComponent;
import cn.fandmc.flametech.gui.impl.utils.RecipeViewerGUI;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 增强工作台按钮
 */
public class EnhancedCraftingTableButton extends UnlockableComponent {

    public EnhancedCraftingTableButton() {
        super(ItemKeys.MULTIBLOCK_ENHANCED_CRAFTING_TABLE, "gui.basic_machines.enhanced_crafting.name");
    }

    @Override
    protected ItemStack createUnlockedDisplay(Player player) {
        return new ItemBuilder(Material.CRAFTING_TABLE)
                .displayName(plugin.getConfigManager().getLang(getDisplayNameKey()))
                .lore(plugin.getConfigManager().getStringList("gui.basic_machines.enhanced_crafting.lore"))
                .build();
    }

    @Override
    protected void onAlreadyUnlocked(Player player, InventoryClickEvent event) {
        try {
            // 已解锁时打开配方查看界面
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    RecipeViewerGUI viewerGUI = RecipeViewerGUI.getInstance(plugin, ItemKeys.ID_ENHANCED_CRAFTING_TABLE);
                    viewerGUI.open(player);
                } catch (Exception e) {
                    MessageUtils.logError("Failed to open recipe viewer: " + e.getMessage());
                    MessageUtils.sendMessage(player, "&c打开配方查看器时发生错误");
                }
            });
        } catch (Exception e) {
            MessageUtils.logError("Error in EnhancedCraftingTableButton: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c操作失败，请稍后重试");
        }
    }
}