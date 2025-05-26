package cn.fandmc.flametech.gui.components;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.unlock.data.UnlockResult;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 可解锁组件基类
 */
public abstract class UnlockableComponent implements GUIComponent {

    protected final String unlockId;
    protected final String displayNameKey;
    protected final Main plugin;

    public UnlockableComponent(String unlockId, String displayNameKey) {
        this.unlockId = unlockId;
        this.displayNameKey = displayNameKey;
        this.plugin = Main.getInstance();
    }

    @Override
    public ItemStack getDisplayItem() {
        // 默认返回锁定状态，子类应该重写此方法提供玩家特定的显示
        return createLockedDisplay();
    }

    /**
     * 为特定玩家获取正确的显示状态
     */
    public ItemStack getDisplayItemForPlayer(Player player) {
        if (plugin.getUnlockManager().isUnlocked(player, unlockId)) {
            return createUnlockedDisplay(player);
        } else {
            return createLockedDisplay();
        }
    }

    @Override
    public final void onClick(Player player, InventoryClickEvent event) {
        try {
            handleUnlockLogic(player, event);
        } catch (Exception e) {
            MessageUtils.logError("Error in UnlockableComponent onClick: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c处理解锁时发生错误");
        }
    }

    private void handleUnlockLogic(Player player, InventoryClickEvent event) {
        // 检查当前解锁状态
        boolean wasUnlocked = plugin.getUnlockManager().isUnlocked(player, unlockId);

        if (!wasUnlocked) {
            // 尝试解锁
            UnlockResult result = plugin.getUnlockManager().unlock(player, unlockId);

            if (result.isSuccess()) {
                // 解锁成功
                String itemName = plugin.getConfigManager().getLang(displayNameKey);
                MessageUtils.sendLocalizedMessage(player, Messages.UNLOCK_SUCCESS,
                        "%item%", itemName);

                // 安全地更新显示为已解锁状态
                updateDisplaySafely(event, createUnlockedDisplay(player));

                // 注意：解锁成功后不自动执行已解锁行为，让玩家再次点击
            } else {
                // 解锁失败，处理错误消息
                handleUnlockFailure(player, result);

                // 确保显示状态正确
                updateDisplaySafely(event, createLockedDisplay());
            }
        } else {
            // 已解锁，执行解锁后的行为
            try {
                onAlreadyUnlocked(player, event);
            } catch (Exception e) {
                MessageUtils.logError("Error in onAlreadyUnlocked: " + e.getMessage());
                MessageUtils.sendMessage(player, "&c执行操作时发生错误");
            }
        }
    }

    /**
     * 安全地更新显示物品
     */
    private void updateDisplaySafely(InventoryClickEvent event, ItemStack newItem) {
        try {
            if (event.getInventory() != null && newItem != null) {
                int slot = event.getSlot();
                if (slot >= 0 && slot < event.getInventory().getSize()) {
                    event.getInventory().setItem(slot, newItem);
                }
            }
        } catch (Exception e) {
            MessageUtils.logError("Failed to update display item: " + e.getMessage());
        }
    }

    private void handleUnlockFailure(Player player, UnlockResult result) {
        switch (result.getStatus()) {
            case INSUFFICIENT_EXP:
                MessageUtils.sendLocalizedMessage(player, Messages.UNLOCK_INSUFFICIENT_EXP,
                        "%required%", String.valueOf(result.getRequiredExp()));
                break;
            case ALREADY_UNLOCKED:
                MessageUtils.sendLocalizedMessage(player, Messages.UNLOCK_ALREADY_UNLOCKED);
                break;
            default:
                MessageUtils.sendMessage(player, "&c解锁失败: " + result.getMessage());
                break;
        }
    }

    /**
     * 创建锁定状态的显示物品
     */
    protected ItemStack createLockedDisplay() {
        String displayName = plugin.getConfigManager().getLang(displayNameKey);
        int requiredExp = plugin.getUnlockManager().getRequiredExp(unlockId);

        List<String> lore = new ArrayList<>();
        lore.add("&7需要经验等级: &e" + requiredExp);
        lore.add("");
        lore.add("&e点击解锁");

        // 允许子类添加额外的lore
        List<String> extraLore = getExtraLockedLore();
        if (extraLore != null) {
            lore.addAll(extraLore);
        }

        return new ItemBuilder(Material.BARRIER)
                .displayName("&c" + displayName + " &7(未解锁)")
                .lore(lore)
                .build();
    }

    /**
     * 子类需要实现：创建解锁状态的显示物品
     */
    protected abstract ItemStack createUnlockedDisplay(Player player);

    /**
     * 子类需要实现：已解锁时的点击行为
     */
    protected abstract void onAlreadyUnlocked(Player player, InventoryClickEvent event);

    /**
     * 子类可以重写：为锁定状态添加额外的lore
     */
    protected List<String> getExtraLockedLore() {
        return null;
    }

    /**
     * 获取解锁ID
     */
    public String getUnlockId() {
        return unlockId;
    }

    /**
     * 获取显示名称键
     */
    public String getDisplayNameKey() {
        return displayNameKey;
    }

    @Override
    public boolean isVisible(Player player) {
        return true;
    }

    @Override
    public boolean isClickable(Player player) {
        return true;
    }
}