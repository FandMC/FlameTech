package cn.fandmc.flametech.unlock.data;

/**
 * 可解锁物品数据类
 */
public class UnlockableItem {

    private final String itemId;
    private final int requiredExp;
    private final String category;
    private final String description;

    public UnlockableItem(String itemId, int requiredExp) {
        this(itemId, requiredExp, "default", "");
    }

    public UnlockableItem(String itemId, int requiredExp, String category) {
        this(itemId, requiredExp, category, "");
    }

    public UnlockableItem(String itemId, int requiredExp, String category, String description) {
        this.itemId = itemId;
        this.requiredExp = Math.max(0, requiredExp);
        this.category = category != null ? category : "default";
        this.description = description != null ? description : "";
    }

    public String getItemId() { return itemId; }
    public int getRequiredExp() { return requiredExp; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UnlockableItem that = (UnlockableItem) obj;
        return itemId.equals(that.itemId);
    }

    @Override
    public int hashCode() {
        return itemId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("UnlockableItem{id='%s', exp=%d, category='%s'}",
                itemId, requiredExp, category);
    }
}