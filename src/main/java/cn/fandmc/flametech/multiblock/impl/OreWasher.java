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

            // 开始洗矿过程
            startWashing(player, location, handItem);

        } catch (Exception e) {
            MessageUtils.logError("Error in ore washer activation: " + e.getMessage());
            MessageUtils.sendMessage(player, "&c处理洗矿机时发生错误");
        }
    }

    private boolean hasCauldronWater(Location location) {
        // 检查炼药锅是否有水（这里简化处理，实际可以检查水位）
        // 在真实实现中可以检查 Levelled 数据
        return true; // 暂时返回true，后续可以完善
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

        // TODO: 目前设置为什么也不掉落，后续可以在这里添加掉落物逻辑
        // giveWashingRewards(player, location);
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