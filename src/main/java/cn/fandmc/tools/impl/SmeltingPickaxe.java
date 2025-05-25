package cn.fandmc.tools.impl;

import cn.fandmc.Main;
import cn.fandmc.tools.SpecialTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class SmeltingPickaxe extends SpecialTool {

    public SmeltingPickaxe(Main plugin) {
        super(plugin, "smelting_pickaxe", "熔炼镐");
    }

    @Override
    public void handleBlockBreak(BlockBreakEvent event, Player player, Block block, ItemStack tool) {
        try {
            Material blockType = block.getType();

            // 检查是否为矿物或变种矿物
            if (canSmelt(blockType)) {
                // 取消原始掉落
                event.setDropItems(false);

                // 获取熔炼后的结果
                Material smeltedMaterial = getSmeltedResult(blockType);

                // 计算掉落数量（基于原始掉落物）
                Collection<ItemStack> originalDrops = getBlockDropsSafely(block, tool);

                Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

                // 掉落熔炼后的物品
                for (ItemStack originalDrop : originalDrops) {
                    if (isRawMaterial(originalDrop.getType()) || originalDrop.getType() == blockType) {
                        // 如果原始掉落是原矿或方块本身，替换为熔炼结果
                        ItemStack smeltedDrop = new ItemStack(smeltedMaterial, originalDrop.getAmount());
                        if (dropLocation.getWorld() != null) {
                            dropLocation.getWorld().dropItemNaturally(dropLocation, smeltedDrop);
                        }
                    } else {
                        // 其他掉落物保持不变（比如经验）
                        if (dropLocation.getWorld() != null) {
                            dropLocation.getWorld().dropItemNaturally(dropLocation, originalDrop);
                        }
                    }
                }

                // 播放熔炼音效
                player.playSound(block.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5f, 1.0f);

                // 显示熔炼粒子效果
                if (block.getWorld() != null) {
                    block.getWorld().spawnParticle(Particle.FLAME,
                            dropLocation, 5, 0.2, 0.2, 0.2, 0.01);
                }
            }

            // 正常消耗耐久度
            damageItem(tool, 1);

        } catch (Exception e) {
            plugin.getLogger().severe("熔炼镐处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 检查材料是否可以被熔炼（仅限矿物和变种矿物）
     */
    private boolean canSmelt(Material material) {
        switch (material) {
            // 铁矿石及变种
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                // 金矿石及变种
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case NETHER_GOLD_ORE:
                // 铜矿石及变种
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                // 煤矿石及变种
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                // 钻石矿石及变种
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                // 绿宝石矿石及变种
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                // 青金石矿石及变种
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                // 红石矿石及变种
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                // 下界石英矿石
            case NETHER_QUARTZ_ORE:
                // 原矿物品
            case RAW_IRON:
            case RAW_GOLD:
            case RAW_COPPER:
                return true;
            default:
                return false;
        }
    }

    /**
     * 获取熔炼映射（仅支持矿物）
     */
    private Material getSmeltedResult(Material material) {
        switch (material) {
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case RAW_IRON:
                return Material.IRON_INGOT;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case NETHER_GOLD_ORE:
            case RAW_GOLD:
                return Material.GOLD_INGOT;
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
            case RAW_COPPER:
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
            case NETHER_QUARTZ_ORE:
                return Material.QUARTZ;
            default:
                return material; // 不能熔炼的返回原材料
        }
    }

    /**
     * 检查是否为原矿材料
     */
    private boolean isRawMaterial(Material material) {
        return material == Material.RAW_IRON ||
                material == Material.RAW_GOLD ||
                material == Material.RAW_COPPER;
    }

    private Collection<ItemStack> getBlockDropsSafely(Block block, ItemStack tool) {
        try {
            return block.getDrops(tool);
        } catch (Exception e) {
            plugin.getLogger().warning("获取方块掉落物时出错: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private void damageItem(ItemStack item, int damage) {
        try {
            if (item != null && item.getType().getMaxDurability() > 0) {
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
            plugin.getLogger().warning("损坏物品时发生错误: " + e.getMessage());
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            return plugin.getConfig().getBoolean("tools.smelting_pickaxe.enabled", true);
        } catch (Exception e) {
            return true;
        }
    }
}