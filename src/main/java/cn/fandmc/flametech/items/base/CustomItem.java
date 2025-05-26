package cn.fandmc.flametech.items.base;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

/**
 * 自定义物品基类
 */
public abstract class CustomItem {

    protected final Main plugin;
    protected final String itemId;
    protected final String displayName;
    protected final String nbtKey;

    public CustomItem(Main plugin, String itemId, String displayName) {
        this.plugin = plugin;
        this.itemId = itemId;
        this.displayName = displayName;
        this.nbtKey = itemId;
    }

    /**
     * 创建物品实例
     */
    public abstract ItemStack createItem();

    /**
     * 检查ItemStack是否为此自定义物品
     */
    public boolean isCustomItem(ItemStack item) {
        return ItemUtils.hasCustomNBT(item, nbtKey);
    }

    /**
     * 获取物品ID
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取NBT键
     */
    public String getNbtKey() {
        return nbtKey;
    }

    /**
     * 检查是否启用
     */
    public boolean isEnabled() {
        return true;
    }
}