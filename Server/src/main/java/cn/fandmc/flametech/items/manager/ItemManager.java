package cn.fandmc.flametech.items.manager;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.items.base.CustomItem;
import cn.fandmc.flametech.items.tools.*;
import cn.fandmc.flametech.utils.ItemUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 物品管理器 - 管理所有自定义物品
 */
public class ItemManager {

    private final Main plugin;
    private final Map<String, CustomItem> customItems = new HashMap<>();

    public ItemManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * 注册默认物品
     */
    public void registerDefaultItems() {
        try {
            // 注册工具
            registerItem(new ExplosivePickaxe(plugin));
            registerItem(new SmeltingPickaxe(plugin));
            registerItem(new MagnetTool(plugin));

            MessageUtils.logInfo("注册了 " + customItems.size() + " 个自定义物品");

        } catch (Exception e) {
            MessageUtils.logError("注册默认物品失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 注册自定义物品
     */
    public void registerItem(CustomItem item) {
        if (item == null) {
            MessageUtils.logWarning("尝试注册空物品");
            return;
        }

        String itemId = item.getItemId();
        if (customItems.containsKey(itemId)) {
            MessageUtils.logWarning("ID为'" + itemId + "'的物品已存在");
            return;
        }

        customItems.put(itemId, item);
    }

    /**
     * 取消注册自定义物品
     */
    public void unregisterItem(String itemId) {
        CustomItem removed = customItems.remove(itemId);
        if (removed != null) {
            MessageUtils.logInfo("已取消注册自定义物品：" + itemId);
        }
    }

    /**
     * 获取自定义物品
     */
    public Optional<CustomItem> getCustomItem(String itemId) {
        return Optional.ofNullable(customItems.get(itemId));
    }

    /**
     * 检查物品是否为自定义物品
     */
    public boolean isCustomItem(ItemStack item) {
        return getCustomItemFromStack(item).isPresent();
    }

    /**
     * 从ItemStack获取自定义物品
     */
    public Optional<CustomItem> getCustomItemFromStack(ItemStack item) {
        if (ItemUtils.isAirOrNull(item)) {
            return Optional.empty();
        }

        // 检查每个注册的自定义物品
        for (CustomItem customItem : customItems.values()) {
            if (customItem.isCustomItem(item)) {
                return Optional.of(customItem);
            }
        }

        return Optional.empty();
    }

    /**
     * 创建指定ID的物品
     */
    public Optional<ItemStack> createItem(String itemId) {
        return getCustomItem(itemId).map(CustomItem::createItem);
    }

    /**
     * 创建爆炸镐
     */
    public ItemStack createExplosivePickaxe() {
        return createItem(ItemKeys.ID_EXPLOSIVE_PICKAXE)
                .orElseThrow(() -> new IllegalStateException("Explosive pickaxe not registered"));
    }

    /**
     * 创建熔炼镐
     */
    public ItemStack createSmeltingPickaxe() {
        return createItem(ItemKeys.ID_SMELTING_PICKAXE)
                .orElseThrow(() -> new IllegalStateException("Smelting pickaxe not registered"));
    }

    /**
     * 创建吸铁石
     */
    public ItemStack createMagnet() {
        return createItem(ItemKeys.ID_MAGNET)
                .orElseThrow(() -> new IllegalStateException("Magnet not registered"));
    }

    /**
     * 检查是否为爆炸镐
     */
    public boolean isExplosivePickaxe(ItemStack item) {
        return getCustomItem(ItemKeys.ID_EXPLOSIVE_PICKAXE)
                .map(customItem -> customItem.isCustomItem(item))
                .orElse(false);
    }

    /**
     * 检查是否为熔炼镐
     */
    public boolean isSmeltingPickaxe(ItemStack item) {
        return getCustomItem(ItemKeys.ID_SMELTING_PICKAXE)
                .map(customItem -> customItem.isCustomItem(item))
                .orElse(false);
    }

    /**
     * 检查是否为吸铁石
     */
    public boolean isMagnet(ItemStack item) {
        return getCustomItem(ItemKeys.ID_MAGNET)
                .map(customItem -> customItem.isCustomItem(item))
                .orElse(false);
    }

    /**
     * 获取所有已注册的物品ID
     */
    public Map<String, CustomItem> getAllCustomItems() {
        return new HashMap<>(customItems);
    }

    /**
     * 获取已注册物品数量
     */
    public int getRegisteredItemCount() {
        return customItems.size();
    }

    /**
     * 清空所有注册的物品
     */
    public void clearAllItems() {
        customItems.clear();
        MessageUtils.logDebug("Cleared all registered custom items");
    }

    /**
     * 重新加载物品
     */
    public void reload() {
        clearAllItems();
        registerDefaultItems();
        MessageUtils.logDebug("Reloaded item manager");
    }
}