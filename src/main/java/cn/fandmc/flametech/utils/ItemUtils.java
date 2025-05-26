package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class ItemUtils {

    /**
     * 创建带有NBT标记的物品
     */
    public static ItemStack createCustomItem(Material material, String displayName, List<String> lore, String nbtKey, String nbtValue) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (displayName != null) {
                meta.setDisplayName(displayName);
            }
            if (lore != null) {
                meta.setLore(lore);
            }
            if (nbtKey != null && nbtValue != null) {
                NamespacedKey key = new NamespacedKey(Main.getInstance(), nbtKey);
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, nbtValue);
            }
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 检查物品是否包含特定的NBT标记
     */
    public static boolean hasCustomNBT(ItemStack item, String nbtKey) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }

        NamespacedKey key = new NamespacedKey(Main.getInstance(), nbtKey);
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    /**
     * 获取物品的NBT值
     */
    public static String getCustomNBTValue(ItemStack item, String nbtKey) {
        if (!hasCustomNBT(item, nbtKey)) {
            return null;
        }

        NamespacedKey key = new NamespacedKey(Main.getInstance(), nbtKey);
        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    /**
     * 安全地损坏物品耐久度
     */
    public static void damageItem(ItemStack item, int damage) {
        if (item == null || damage <= 0) {
            return;
        }

        try {
            if (item.getType().getMaxDurability() > 0) {
                org.bukkit.inventory.meta.Damageable meta = (org.bukkit.inventory.meta.Damageable) item.getItemMeta();
                if (meta != null) {
                    int currentDamage = meta.getDamage();
                    int newDamage = currentDamage + damage;

                    if (newDamage >= item.getType().getMaxDurability()) {
                        item.setType(Material.AIR);
                    } else {
                        meta.setDamage(newDamage);
                        item.setItemMeta(meta);
                    }
                }
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("Failed to damage item: " + e.getMessage());
        }
    }

    /**
     * 检查物品是否为空气或null
     */
    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getType().isAir();
    }

    /**
     * 克隆物品堆叠，安全处理null情况
     */
    public static ItemStack cloneSafely(ItemStack item) {
        return item != null ? item.clone() : null;
    }

    /**
     * 比较两个物品是否相似（类型和数据相同，忽略数量）
     */
    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) {
            return item1 == item2;
        }
        return item1.isSimilar(item2);
    }

    /**
     * 检查物品是否可以用镐子挖掘
     */
    public static boolean canBreakWithPickaxe(Material material) {
        switch (material) {
            case STONE:
            case COBBLESTONE:
            case MOSSY_COBBLESTONE:
            case STONE_BRICKS:
            case MOSSY_STONE_BRICKS:
            case CRACKED_STONE_BRICKS:
            case CHISELED_STONE_BRICKS:
            case GRANITE:
            case POLISHED_GRANITE:
            case DIORITE:
            case POLISHED_DIORITE:
            case ANDESITE:
            case POLISHED_ANDESITE:
            case DEEPSLATE:
            case COBBLED_DEEPSLATE:
            case POLISHED_DEEPSLATE:
            case DEEPSLATE_BRICKS:
            case CRACKED_DEEPSLATE_BRICKS:
            case DEEPSLATE_TILES:
            case CRACKED_DEEPSLATE_TILES:
            case CHISELED_DEEPSLATE:
            case TUFF:
            case CALCITE:
            case DRIPSTONE_BLOCK:
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
            case NETHER_GOLD_ORE:
            case NETHER_QUARTZ_ORE:
            case SANDSTONE:
            case RED_SANDSTONE:
            case NETHERRACK:
            case BLACKSTONE:
            case BASALT:
            case SMOOTH_BASALT:
            case END_STONE:
            case PURPUR_BLOCK:
            case PURPUR_PILLAR:
                return true;
            default:
                return false;
        }
    }

    private ItemUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}