package cn.fandmc.gui.components;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.unlock.UnlockManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class UnlockableButton implements GUIComponent {
    protected final String unlockId;
    protected final String displayNameKey;

    public UnlockableButton(String unlockId, String displayNameKey) {
        this.unlockId = unlockId;
        this.displayNameKey = displayNameKey;
    }

    @Override
    public ItemStack item() {
        // 默认返回锁定状态，实际状态由GUI动态处理
        return createLockedDisplay();
    }

    /**
     * 为特定玩家获取正确的显示状态
     */
    public ItemStack getItemForPlayer(Player player) {
        if (UnlockManager.getInstance().isUnlocked(player, unlockId)) {
            return createUnlockedDisplay();
        } else {
            return createLockedDisplay();
        }
    }

    @Override
    public final void onClick(Player player, InventoryClickEvent event) {
        // 统一的解锁逻辑处理
        handleUnlockLogic(player, event);
    }

    private void handleUnlockLogic(Player player, InventoryClickEvent event) {
        // 总是先更新显示状态
        ItemStack correctItem = getItemForPlayer(player);
        event.getInventory().setItem(event.getSlot(), correctItem);

        if (!UnlockManager.getInstance().isUnlocked(player, unlockId)) {
            // 尝试解锁
            UnlockManager.UnlockResult result = UnlockManager.getInstance().unlock(player, unlockId);

            if (result.isSuccess()) {
                // 解锁成功
                String itemName = Main.getInstance().getConfigManager().getLang(displayNameKey);
                player.sendMessage(Main.getInstance().getConfigManager().getLang("unlock.success")
                        .replace("%item%", itemName));
                // 更新显示为已解锁状态
                event.getInventory().setItem(event.getSlot(), createUnlockedDisplay());

                // 注意：解锁成功后不自动执行已解锁行为，让玩家再次点击
            } else {
                // 解锁失败，处理错误消息
                handleUnlockFailure(player, result);
            }
        } else {
            // 已解锁，执行解锁后的行为
            onAlreadyUnlocked(player, event);
        }
    }

    private void handleUnlockFailure(Player player, UnlockManager.UnlockResult result) {
        switch (result.getMessage()) {
            case "insufficient_exp":
                player.sendMessage(Main.getInstance().getConfigManager().getLang("unlock.insufficient_exp")
                        .replace("%required%", String.valueOf(result.getRequiredExp())));
                break;
            case "already_unlocked":
                player.sendMessage(Main.getInstance().getConfigManager().getLang("unlock.already_unlocked"));
                break;
            default:
                player.sendMessage("§c解锁失败: " + result.getMessage());
                break;
        }
    }

    /**
     * 创建锁定状态的显示物品
     */
    protected ItemStack createLockedDisplay() {
        ItemStack locked = new ItemStack(Material.BARRIER);
        ItemMeta meta = locked.getItemMeta();
        if (meta != null) {
            String displayName = Main.getInstance().getConfigManager().getLang(displayNameKey);
            meta.setDisplayName("§c" + displayName + " §7(未解锁)");

            List<String> lore = new ArrayList<>();
            lore.add("§7需要经验等级: §e" + UnlockManager.getInstance().getRequiredExp(unlockId));
            lore.add("");
            lore.add("§e点击解锁");

            // 允许子类添加额外的lore
            List<String> extraLore = getExtraLockedLore();
            if (extraLore != null) {
                lore.addAll(extraLore);
            }

            meta.setLore(lore);
            locked.setItemMeta(meta);
        }
        return locked;
    }

    /**
     * 子类需要实现：创建解锁状态的显示物品
     */
    protected abstract ItemStack createUnlockedDisplay();

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
}