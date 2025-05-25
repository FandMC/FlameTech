package cn.fandmc.tools;

import cn.fandmc.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ToolManager {
    private static ToolManager instance;
    private final Main plugin;

    // 工具类型的NBT键
    private final NamespacedKey explosivePickaxeKey;
    private final NamespacedKey smeltingPickaxeKey;

    private ToolManager(Main plugin) {
        this.plugin = plugin;
        this.explosivePickaxeKey = new NamespacedKey(plugin, "explosive_pickaxe");
        this.smeltingPickaxeKey = new NamespacedKey(plugin, "smelting_pickaxe");
    }

    public static void init(Main plugin) {
        if (instance == null) {
            instance = new ToolManager(plugin);
        }
    }

    public static ToolManager getInstance() {
        return instance;
    }

    /**
     * 检查是否为爆炸镐
     */
    public boolean isExplosivePickaxe(ItemStack item) {
        if (item == null || item.getType() != Material.IRON_PICKAXE) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(explosivePickaxeKey, PersistentDataType.BYTE);
    }

    /**
     * 检查是否为熔炼镐
     */
    public boolean isSmeltingPickaxe(ItemStack item) {
        if (item == null || item.getType() != Material.IRON_PICKAXE) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(smeltingPickaxeKey, PersistentDataType.BYTE);
    }

    /**
     * 创建爆炸镐
     */
    public ItemStack createExplosivePickaxe() {
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§l爆炸镐");
            meta.setLore(java.util.Arrays.asList(
                    "§7一把充满破坏力的镐子",
                    "§7能够炸毁大片区域的方块",
                    "§c小心使用！",
                    "",
                    "§e功能:",
                    "§7• 破坏" + getExplosionRadius() + "格半径内的方块",
                    "§7• 产生爆炸效果和音效",
                    "§7• 消耗额外耐久度",
                    "",
                    "§e[FlameTech 工具]"
            ));

            // 设置NBT标记
            meta.getPersistentDataContainer().set(explosivePickaxeKey, PersistentDataType.BYTE, (byte) 1);
            pickaxe.setItemMeta(meta);
        }

        return pickaxe;
    }

    /**
     * 创建熔炼镐
     */
    public ItemStack createSmeltingPickaxe() {
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6§l熔炼镐");
            meta.setLore(java.util.Arrays.asList(
                    "§7一把能够自动熔炼的镐子",
                    "§7挖掘矿物时自动熔炼成锭",
                    "§6节省熔炉燃料！",
                    "",
                    "§e功能:",
                    "§7• 矿石直接掉落锭",
                    "§7• 石头变成平滑石头",
                    "§7• 沙子变成玻璃",
                    "§7• 播放熔炼音效和粒子",
                    "",
                    "§e[FlameTech 工具]"
            ));

            // 设置NBT标记
            meta.getPersistentDataContainer().set(smeltingPickaxeKey, PersistentDataType.BYTE, (byte) 1);
            pickaxe.setItemMeta(meta);
        }

        return pickaxe;
    }

    /**
     * 获取爆炸半径（从配置文件读取，带安全限制）
     */
    public int getExplosionRadius() {
        try {
            FileConfiguration config = plugin.getConfig();
            int radius = config.getInt("tools.explosive_pickaxe.explosion_radius", 2);

            // 安全限制：最小1，最大3
            return Math.max(1, Math.min(3, radius));
        } catch (Exception e) {
            plugin.getLogger().warning("读取爆炸半径配置失败，使用默认值2: " + e.getMessage());
            return 2;
        }
    }

    /**
     * 获取最大方块处理数量
     */
    public int getMaxBlocksPerExplosion() {
        try {
            FileConfiguration config = plugin.getConfig();
            int maxBlocks = config.getInt("tools.explosive_pickaxe.max_blocks", 50);

            // 安全限制：最小5，最大100
            return Math.max(5, Math.min(100, maxBlocks));
        } catch (Exception e) {
            plugin.getLogger().warning("读取最大方块数配置失败，使用默认值50: " + e.getMessage());
            return 50;
        }
    }

    /**
     * 检查是否启用爆炸镐
     */
    public boolean isExplosivePickaxeEnabled() {
        try {
            return plugin.getConfig().getBoolean("tools.explosive_pickaxe.enabled", true);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 检查是否启用熔炼镐
     */
    public boolean isSmeltingPickaxeEnabled() {
        try {
            return plugin.getConfig().getBoolean("tools.smelting_pickaxe.enabled", true);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取熔炼映射
     */
    public Material getSmeltedResult(Material material) {
        switch (material) {
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                return Material.IRON_INGOT;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case NETHER_GOLD_ORE:
                return Material.GOLD_INGOT;
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                return Material.COPPER_INGOT;
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                return Material.COAL;
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                return Material.DIAMOND;
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                return Material.EMERALD;
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                return Material.LAPIS_LAZULI;
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                return Material.REDSTONE;
            case COBBLESTONE:
                return Material.STONE;
            case STONE:
                return Material.SMOOTH_STONE;
            case SAND:
                return Material.GLASS;
            case CLAY:
                return Material.TERRACOTTA;
            case NETHERRACK:
                return Material.NETHER_BRICK;
            case RAW_IRON:
                return Material.IRON_INGOT;
            case RAW_GOLD:
                return Material.GOLD_INGOT;
            case RAW_COPPER:
                return Material.COPPER_INGOT;
            default:
                return material; // 不能熔炼的返回原材料
        }
    }

    /**
     * 检查材料是否可以被熔炼
     */
    public boolean canSmelt(Material material) {
        return getSmeltedResult(material) != material;
    }
}