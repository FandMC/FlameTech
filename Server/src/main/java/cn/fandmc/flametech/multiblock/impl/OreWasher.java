package cn.fandmc.flametech.multiblock.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.multiblock.base.BlockOffset;
import cn.fandmc.flametech.multiblock.base.MultiblockStructure;
import cn.fandmc.flametech.utils.FoliaUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 洗矿机多方块结构
 * 结构：
 * 0 0 0  (Y+1)
 * 0 h 0  (Y+0) - 主方块层
 * 0 t 0  (Y-1)
 *
 * h = 活版门 (主方块)
 * t = 炼药锅
 */
public class OreWasher extends MultiblockStructure {

    // 存储正在洗矿的位置
    private static final Map<String, Long> washingLocations = new ConcurrentHashMap<>();
    private static final long WASHING_TIME_MIN = 3000; // 3秒
    private static final long WASHING_TIME_MAX = 5000; // 5秒

    // 洗矿奖励配置
    private static final double GRAVEL_CHANCE = 0.30;      // 30% 碎石
    private static final double ORE_DUST_CHANCE = 0.60;    // 60% 矿粉
    private static final double NOTHING_CHANCE = 0.10;     // 10% 无奖励

    public OreWasher(Main plugin) {
        super(plugin, "ore_washer",
                plugin.getConfigManager().getLang("multiblock.ore_washer.name"),
                createStructure(),
                3);
    }

    private static Map<BlockOffset, Material> createStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();

        // Y+0 层：活版门（主方块）
        structure.put(new BlockOffset(0, 0, 0), Material.OAK_TRAPDOOR);

        // Y-1 层：炼药锅
        structure.put(new BlockOffset(0, -1, 0), Material.CAULDRON);

        return structure;
    }

    @Override
    public void onActivate(Player player, Location location, PlayerInteractEvent event) {
        if (!canUse(player)) {
            MessageUtils.sendLocalizedMessage(player, Messages.MULTIBLOCK_NOT_UNLOCKED,
                    "%machine%", getDisplayName());
            return;
        }

        try {
            ItemStack handItem = player.getInventory().getItemInMainHand();

            // 检查玩家是否手持沙砾
            if (handItem == null || handItem.getType() != Material.GRAVEL) {
                MessageUtils.sendLocalizedMessage(player, "multiblock.ore_washer.need_gravel");
                return;
            }

            // 检查炼药锅是否有水
            if (!hasCauldronWater(location)) {
                MessageUtils.sendLocalizedMessage(player, "multiblock.ore_washer.need_water");
                return;
            }

            // 检查是否已经在洗矿
            if (isWashing(location)) {
                int remainingTime = getRemainingTime(location);
                MessageUtils.sendLocalizedMessage(player, "multiblock.ore_washer.already_washing",
                        "%time%", String.valueOf(remainingTime));
                return;
            }

            // 开始洗矿过程
            startWashing(player, location, handItem);

        } catch (Exception e) {
            MessageUtils.logError("Error in ore washer activation: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c处理洗矿机时发生错误");
        }
    }

    private boolean hasCauldronWater(Location location) {
        Block cauldronBlock = location.clone().add(0, -1, 0).getBlock();

        if (cauldronBlock.getType() != Material.CAULDRON) {
            return false;
        }

        // 检查炼药锅的水位
        org.bukkit.block.data.Levelled cauldronData = (org.bukkit.block.data.Levelled) cauldronBlock.getBlockData();
        return cauldronData.getLevel() > 0;
    }

    private void startWashing(Player player, Location location, ItemStack gravelItem) {
        // 消耗一个沙砾
        gravelItem.setAmount(gravelItem.getAmount() - 1);
        if (gravelItem.getAmount() <= 0) {
            player.getInventory().setItemInMainHand(null);
        }

        // 播放开始洗矿的音效
        if (location.getWorld() != null) {
            location.getWorld().playSound(location, Sound.ITEM_BUCKET_FILL, 1.0f, 1.2f);
        }

        // 记录洗矿位置
        String locationKey = getLocationKey(location);
        long washingTime = ThreadLocalRandom.current().nextLong(WASHING_TIME_MIN, WASHING_TIME_MAX + 1);
        washingLocations.put(locationKey, System.currentTimeMillis() + washingTime);

        // 发送开始消息
        MessageUtils.sendLocalizedMessage(player, "multiblock.ore_washer.washing_started");

        // 开始粒子效果循环
        startWashingEffects(location, washingTime);

        // 设置洗矿完成的任务
        FoliaUtils.runTaskLater(location, () -> {
            completeWashing(player, location);
        }, washingTime / 50); // 转换为tick
    }

    private void startWashingEffects(Location location, long duration) {
        Location effectLocation = location.clone().add(0.5, 0.5, 0.5);

        // 计算粒子效果的次数
        long particleTicks = duration / 100; // 每100ms一次粒子效果

        for (int i = 0; i < particleTicks; i++) {
            FoliaUtils.runTaskLater(location, () -> {
                if (location.getWorld() != null) {
                    // 洗矿粒子效果
                    location.getWorld().spawnParticle(Particle.DRIPPING_WATER,
                            effectLocation, 3, 0.3, 0.1, 0.3, 0);
                    location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
                            effectLocation, 5, 0.2, 0.1, 0.2, 0, Material.GRAVEL.createBlockData());

                    // 偶尔播放水滴声
                    if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                        location.getWorld().playSound(location, Sound.BLOCK_POINTED_DRIPSTONE_DRIP_WATER, 0.5f, 1.0f);
                    }
                }
            }, i * 2); // 每2tick播放一次
        }
    }

    private void completeWashing(Player player, Location location) {
        String locationKey = getLocationKey(location);
        washingLocations.remove(locationKey);

        // 播放完成音效
        if (location.getWorld() != null) {
            location.getWorld().playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
            location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                    location.clone().add(0.5, 0.5, 0.5), 8, 0.3, 0.3, 0.3, 0);
        }

        // 给予洗矿奖励
        giveWashingRewards(player, location);
    }

    /**
     * 给予洗矿奖励
     */
    private void giveWashingRewards(Player player, Location location) {
        double random = ThreadLocalRandom.current().nextDouble();

        ItemStack reward = null;
        String rewardMessage = null;

        if (random < GRAVEL_CHANCE) {
            // 30% 获得碎石
            reward = new ItemStack(Material.COBBLESTONE, 1);
            rewardMessage = "&7你获得了 &f碎石 &7x1";

        } else if (random < GRAVEL_CHANCE + ORE_DUST_CHANCE) {
            // 60% 获得矿粉
            reward = createOreDust();
            if (reward != null) {
                rewardMessage = "&a你获得了 &e矿粉 &ax1！";
            } else {
                MessageUtils.logError("无法创建矿粉物品");
                rewardMessage = "&c获取矿粉失败，请联系管理员";
            }

        } else {
            // 10% 什么都没有
            rewardMessage = "&7真遗憾，这次什么都没洗出来...";

            // 播放失败音效
            if (location.getWorld() != null) {
                location.getWorld().playSound(location, Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f);
            }
        }

        // 给予奖励
        if (reward != null) {
            Location dropLocation = location.clone().add(0.5, 0.5, 0.5);

            // 尝试直接给玩家，如果背包满了就掉落
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(reward.clone());

            if (!leftover.isEmpty()) {
                // 背包满了，掉落在地上
                for (ItemStack overflow : leftover.values()) {
                    if (dropLocation.getWorld() != null) {
                        dropLocation.getWorld().dropItemNaturally(dropLocation, overflow);
                    }
                }
                rewardMessage += " &7(背包已满，物品掉落在地上)";
            }

            // 播放获得物品的音效
            if (location.getWorld() != null) {
                location.getWorld().playSound(location, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
            }
        }

        // 发送消息
        if (rewardMessage != null) {
            MessageUtils.sendMessage(player, rewardMessage);
        }
    }

    /**
     * 创建矿粉物品
     */
    private ItemStack createOreDust() {
        try {
            // 尝试从材料管理器获取矿粉
            return plugin.getMaterialManager().createMaterial("ore_dust").orElse(null);
        } catch (Exception e) {
            MessageUtils.logError("创建矿粉失败: " + e.getMessage());
            return null;
        }
    }

    private String getLocationKey(Location location) {
        return location.getWorld().getName() + "_" +
                location.getBlockX() + "_" +
                location.getBlockY() + "_" +
                location.getBlockZ();
    }

    /**
     * 检查指定位置是否正在洗矿
     */
    public static boolean isWashing(Location location) {
        String locationKey = location.getWorld().getName() + "_" +
                location.getBlockX() + "_" +
                location.getBlockY() + "_" +
                location.getBlockZ();

        Long endTime = washingLocations.get(locationKey);
        if (endTime == null) {
            return false;
        }

        if (System.currentTimeMillis() > endTime) {
            washingLocations.remove(locationKey);
            return false;
        }

        return true;
    }

    /**
     * 获取洗矿剩余时间（秒）
     */
    public static int getRemainingTime(Location location) {
        String locationKey = location.getWorld().getName() + "_" +
                location.getBlockX() + "_" +
                location.getBlockY() + "_" +
                location.getBlockZ();

        Long endTime = washingLocations.get(locationKey);
        if (endTime == null) {
            return 0;
        }

        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, (int) (remaining / 1000));
    }

    @Override
    public boolean canCraft(String recipeId) {
        // 洗矿机不使用传统配方系统
        return false;
    }
}