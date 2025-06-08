package cn.fandmc.flametech.items.builders;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Builder pattern for creating ItemStacks with various properties
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = itemStack.getItemMeta();

        if (this.itemMeta == null) {
            throw new IllegalArgumentException("Material " + material + " does not support ItemMeta");
        }
    }

    public ItemBuilder(ItemStack baseItem) {
        this.itemStack = baseItem.clone();
        this.itemMeta = itemStack.getItemMeta();

        if (this.itemMeta == null) {
            throw new IllegalArgumentException("ItemStack does not support ItemMeta");
        }
    }

    /**
     * 设置显示名称
     */
    public ItemBuilder displayName(String name) {
        if (name != null) {
            itemMeta.setDisplayName(MessageUtils.colorize(name));
        }
        return this;
    }

    /**
     * 设置Lore
     */
    public ItemBuilder lore(String... loreLines) {
        return lore(Arrays.asList(loreLines));
    }

    /**
     * 设置Lore
     */
    public ItemBuilder lore(List<String> lore) {
        if (lore != null) {
            itemMeta.setLore(MessageUtils.colorize(lore));
        }
        return this;
    }

    /**
     * 添加Lore行
     */
    public ItemBuilder addLore(String... lines) {
        List<String> currentLore = itemMeta.getLore();
        if (currentLore == null) {
            currentLore = new ArrayList<>();
        }

        currentLore.addAll(Arrays.asList(lines));
        return lore(currentLore);
    }

    /**
     * 设置数量
     */
    public ItemBuilder amount(int amount) {
        itemStack.setAmount(Math.max(1, Math.min(64, amount)));
        return this;
    }

    /**
     * 添加附魔
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * 添加多个附魔
     */
    public ItemBuilder enchants(Map<Enchantment, Integer> enchantments) {
        if (enchantments != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                enchant(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * 设置物品标志
     */
    public ItemBuilder flags(ItemFlag... flags) {
        if (flags != null) {
            itemMeta.addItemFlags(flags);
        }
        return this;
    }

    /**
     * 隐藏所有标志
     */
    public ItemBuilder hideAllFlags() {
        return flags(ItemFlag.values());
    }

    /**
     * 设置自定义模型数据
     */
    public ItemBuilder customModelData(int data) {
        itemMeta.setCustomModelData(data);
        return this;
    }

    /**
     * 设置不可破坏
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * 添加NBT数据（字符串）
     */
    public ItemBuilder nbt(String key, String value) {
        if (key != null && value != null) {
            NamespacedKey namespacedKey = new NamespacedKey(Main.getInstance(), key);
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        }
        return this;
    }

    /**
     * 添加NBT数据（整数）
     */
    public ItemBuilder nbt(String key, int value) {
        if (key != null) {
            NamespacedKey namespacedKey = new NamespacedKey(Main.getInstance(), key);
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, value);
        }
        return this;
    }

    /**
     * 添加NBT数据（布尔值）
     */
    public ItemBuilder nbt(String key, boolean value) {
        if (key != null) {
            NamespacedKey namespacedKey = new NamespacedKey(Main.getInstance(), key);
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BYTE, (byte) (value ? 1 : 0));
        }
        return this;
    }

    /**
     * 设置发光效果（添加附魔并隐藏）
     */
    public ItemBuilder glow() {
        return glow(true);
    }

    /**
     * 设置发光效果
     */
    public ItemBuilder glow(boolean glow) {
        if (glow) {
            enchant(Enchantment.UNBREAKING, 1);
            flags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * 构建最终的ItemStack
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * 快速创建简单物品的静态方法
     */
    public static ItemStack createSimpleItem(Material material, String name) {
        return new ItemBuilder(material).displayName(name).build();
    }

    /**
     * 快速创建带Lore的物品的静态方法
     */
    public static ItemStack createSimpleItem(Material material, String name, String... lore) {
        return new ItemBuilder(material).displayName(name).lore(lore).build();
    }

    /**
     * 创建边框物品
     */
    public static ItemStack createBorderItem() {
        try {
            String displayName = Main.getInstance().getConfigManager().getLang(Messages.ITEM_BUILDER_BORDER_ITEM_NAME);
            return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .displayName(displayName)
                    .build();
        } catch (Exception e) {
            // 如果配置加载失败，使用默认值
            return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .displayName(" ")
                    .build();
        }
    }

    /**
     * 创建返回按钮
     */
    public static ItemStack createBackButton() {
        try {
            String displayName = Main.getInstance().getConfigManager().getLang(Messages.ITEM_BUILDER_BACK_BUTTON_NAME);
            return new ItemBuilder(Material.ARROW)
                    .displayName(displayName)
                    .build();
        } catch (Exception e) {
            // 如果配置加载失败，使用默认值
            return new ItemBuilder(Material.ARROW)
                    .displayName("&a← 返回")
                    .build();
        }
    }

    /**
     * 创建下一页按钮
     */
    public static ItemStack createNextPageButton() {
        try {
            String displayName = Main.getInstance().getConfigManager().getLang(Messages.ITEM_BUILDER_NEXT_PAGE_BUTTON_NAME);
            return new ItemBuilder(Material.ARROW)
                    .displayName(displayName)
                    .build();
        } catch (Exception e) {
            // 如果配置加载失败，使用默认值
            return new ItemBuilder(Material.ARROW)
                    .displayName("&a下一页 →")
                    .build();
        }
    }

    /**
     * 创建上一页按钮
     */
    public static ItemStack createPreviousPageButton() {
        try {
            String displayName = Main.getInstance().getConfigManager().getLang(Messages.ITEM_BUILDER_PREVIOUS_PAGE_BUTTON_NAME);
            return new ItemBuilder(Material.ARROW)
                    .displayName(displayName)
                    .build();
        } catch (Exception e) {
            // 如果配置加载失败，使用默认值
            return new ItemBuilder(Material.ARROW)
                    .displayName("&a← 上一页")
                    .build();
        }
    }

    /**
     * 创建页面信息物品
     */
    public static ItemStack createPageInfoItem(int currentPage, int totalPages, int totalItems) {
        try {
            String displayName = Main.getInstance().getConfigManager().getLang(Messages.ITEM_BUILDER_PAGE_INFO_NAME);

            // 获取lore列表并替换参数
            List<String> lore = Main.getInstance().getConfigManager().getStringList("items.buttons.page_info.lore");
            List<String> processedLore = new ArrayList<>();

            for (String line : lore) {
                String processedLine = line
                        .replace("%current%", String.valueOf(currentPage))
                        .replace("%total%", String.valueOf(totalPages))
                        .replace("%items%", String.valueOf(totalItems));
                processedLore.add(processedLine);
            }

            return new ItemBuilder(Material.BOOK)
                    .displayName(displayName)
                    .lore(processedLore)
                    .build();
        } catch (Exception e) {
            // 如果配置加载失败，使用默认值
            return new ItemBuilder(Material.BOOK)
                    .displayName("&e页面信息")
                    .lore(
                            "&7当前页: &e" + currentPage + "&7/&e" + totalPages,
                            "&7物品总数: &e" + totalItems
                    )
                    .build();
        }
    }

    /**
     * 安全获取配置管理器的工具方法
     */
    private static String getSafeConfig(String key, String defaultValue) {
        try {
            return Main.getInstance().getConfigManager().getLang(key);
        } catch (Exception e) {
            MessageUtils.logWarning("Failed to load config for key: " + key + ", using default value");
            return defaultValue;
        }
    }
}