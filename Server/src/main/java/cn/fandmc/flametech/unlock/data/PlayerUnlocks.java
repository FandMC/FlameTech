package cn.fandmc.flametech.unlock.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 玩家解锁数据
 */
public class PlayerUnlocks {

    private final UUID playerId;
    private final Set<String> unlockedItems = new HashSet<>();
    private long lastSaveTime;

    public PlayerUnlocks(UUID playerId) {
        this.playerId = playerId;
        this.lastSaveTime = System.currentTimeMillis();
    }

    /**
     * 解锁物品
     */
    public boolean unlock(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return false;
        }

        boolean added = unlockedItems.add(itemId);
        if (added) {
            updateSaveTime();
        }
        return added;
    }

    /**
     * 检查物品是否已解锁
     */
    public boolean isUnlocked(String itemId) {
        return itemId != null && unlockedItems.contains(itemId);
    }

    /**
     * 获取所有已解锁的物品
     */
    public Set<String> getUnlockedItems() {
        return new HashSet<>(unlockedItems);
    }

    /**
     * 获取已解锁物品数量
     */
    public int getUnlockedCount() {
        return unlockedItems.size();
    }

    /**
     * 批量添加解锁物品
     */
    public void addUnlockedItems(Set<String> items) {
        if (items != null && !items.isEmpty()) {
            unlockedItems.addAll(items);
            updateSaveTime();
        }
    }

    /**
     * 移除解锁物品（用于重置）
     */
    public boolean removeUnlock(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return false;
        }

        boolean removed = unlockedItems.remove(itemId);
        if (removed) {
            updateSaveTime();
        }
        return removed;
    }

    /**
     * 清空所有解锁
     */
    public void clearAllUnlocks() {
        if (!unlockedItems.isEmpty()) {
            unlockedItems.clear();
            updateSaveTime();
        }
    }

    /**
     * 检查指定类别的解锁进度
     */
    public int getUnlockedCountByCategory(String category, Set<String> categoryItems) {
        if (category == null || categoryItems == null) {
            return 0;
        }

        return (int) categoryItems.stream()
                .filter(unlockedItems::contains)
                .count();
    }

    private void updateSaveTime() {
        this.lastSaveTime = System.currentTimeMillis();
    }

    public UUID getPlayerId() { return playerId; }
    public long getLastSaveTime() { return lastSaveTime; }

    @Override
    public String toString() {
        return String.format("PlayerUnlocks{player=%s, unlocked=%d}",
                playerId, unlockedItems.size());
    }
}