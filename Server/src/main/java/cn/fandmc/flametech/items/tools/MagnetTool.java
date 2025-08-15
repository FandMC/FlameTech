package cn.fandmc.flametech.items.tools;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.items.base.SpecialTool;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.utils.FoliaUtils;
import cn.fandmc.flametech.utils.ItemUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 吸铁石工具
 */
public class MagnetTool extends SpecialTool {

    private static final double ATTRACT_RANGE = 8.0;
    private static final long COOLDOWN_MS = 3000;
    private static final int MAX_ITEMS_PER_USE = 30;
    private static final boolean ATTRACT_EXPERIENCE = true;
    private static final int ATTRACT_DELAY_TICKS = 10;

    // 冷却记录
    private static final Map<UUID, Long> playerCooldowns = new HashMap<>();

    public MagnetTool(Main plugin) {
        super(plugin, ItemKeys.ID_MAGNET,
                plugin.getConfigManager().getSafeLang(Messages.ITEMS_MAGNET_NAME, "吸铁石"));
    }

    @Override
    public ItemStack createItem() {
        String displayName = plugin.getConfigManager().getLang(Messages.ITEMS_MAGNET_NAME);
        List<String> lore = plugin.getConfigManager().getStringList(Messages.ITEMS_MAGNET_LORE);

        return new ItemBuilder(Material.COMPASS)
                .displayName(displayName)
                .lore(lore)
                .nbt(nbtKey, "true")
                .glow()
                .build();
    }

    @Override
    public void handleBlockBreak(BlockBreakEvent event, Player player, Block block, ItemStack tool) {
        // 吸铁石不处理方块破坏事件
    }

    /**
     * 处理右键点击事件
     */
    public void handleRightClick(PlayerInteractEvent event, Player player, ItemStack tool) {
        // 调试信息
        MessageUtils.logDebug("MagnetTool.handleRightClick called for player: " + player.getName());


        // 基础验证
        if (!ValidationUtils.isValidPlayer(player)) {
            MessageUtils.logError("Invalid player in magnet handleRightClick");
            return;
        }

        if (!ValidationUtils.isValidItem(tool)) {
            MessageUtils.logError("Invalid tool in magnet handleRightClick");
            return;
        }

        // 检查冷却
        if (isOnCooldown(player)) {
            sendCooldownMessage(player);
            return;
        }

        MessageUtils.logDebug("Magnet not on cooldown, attempting to attract items...");


        // 执行吸引
        attractNearbyItemsAsync(player, tool);
    }

    /**
     * 异步吸引附近的物品和经验球
     */
    private void attractNearbyItemsAsync(Player player, ItemStack tool) {
        Location playerLoc = player.getLocation();

        if (playerLoc.getWorld() == null) {
            MessageUtils.logError("Player world is null in attractNearbyItemsAsync");
            return;
        }

        try {
            // 获取附近的实体
            List<Entity> nearbyEntities = playerLoc.getWorld().getNearbyEntities(
                            playerLoc, ATTRACT_RANGE, ATTRACT_RANGE, ATTRACT_RANGE)
                    .stream()
                    .filter(this::canAttractEntity)
                    .limit(MAX_ITEMS_PER_USE)
                    .collect(Collectors.toList());

            if (nearbyEntities.isEmpty()) {
                // 没有找到物品
                MessageUtils.sendLocalizedMessage(player, Messages.TOOLS_MAGNET_NO_ITEMS);

                MessageUtils.logDebug("No attractable entities found in range");

                return;
            }

            MessageUtils.logDebug("Found " + nearbyEntities.size() + " attractable entities");


            // 播放音效
            player.playSound(playerLoc, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.5f);

            // 设置冷却和消耗耐久（在开始吸引时就处理）
            setCooldown(player);
            ItemUtils.damageItem(tool, 1);

            // 发送成功消息
            MessageUtils.sendLocalizedMessage(player, Messages.TOOLS_MAGNET_SUCCESS,
                    "%count%", String.valueOf(nearbyEntities.size()));

            // 异步吸引每个实体
            for (Entity entity : nearbyEntities) {
                attractEntityAsync(entity, player);
            }

        } catch (Exception e) {
            MessageUtils.logError("Error in attractNearbyItemsAsync: " + e.getMessage());
        }
    }

    /**
     * 检查实体是否可以被吸引
     */
    private boolean canAttractEntity(Entity entity) {
        if (entity instanceof Item) {
            Item item = (Item) entity;
            return item.isValid() &&
                    !ItemUtils.isAirOrNull(item.getItemStack()) &&
                    item.getPickupDelay() <= 0;
        }

        if (ATTRACT_EXPERIENCE && entity instanceof ExperienceOrb) {
            return entity.isValid();
        }

        return false;
    }

    /**
     * 异步吸引单个实体到玩家位置
     */
    private void attractEntityAsync(Entity entity, Player player) {
        Location entityLoc = entity.getLocation();
        Location targetLoc = player.getLocation().add(0, 0.5, 0);

        // 创建粒子轨迹
        createAttractParticles(entityLoc, targetLoc);

        FoliaUtils.runTaskLater(entityLoc, () -> {
            if (entity.isValid() && player.isOnline()) {
                CompletableFuture<Boolean> teleportFuture = FoliaUtils.teleportAsync(entity, targetLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

                teleportFuture.thenAccept(success -> {
                    if (success && entityLoc.getWorld() != null) {
                        // 传送成功后播放粒子效果
                        FoliaUtils.runTask(targetLoc, () -> {
                            if (targetLoc.getWorld() != null) {
                                targetLoc.getWorld().spawnParticle(
                                        Particle.ELECTRIC_SPARK,
                                        targetLoc, 5, 0.2, 0.2, 0.2, 0.1);
                            }
                        });
                    }
                }).exceptionally(throwable -> {
                    MessageUtils.logDebug("Failed to teleport entity: " + throwable.getMessage());
                    return null;
                });
            }
        }, ATTRACT_DELAY_TICKS);
    }

    /**
     * 创建吸引粒子效果
     */
    private void createAttractParticles(Location from, Location to) {
        if (from.getWorld() == null) return;

        try {
            // 创建从物品到玩家的粒子轨迹
            double distance = from.distance(to);
            int particleCount = (int) Math.min(distance * 2, 20);

            FoliaUtils.runTask(from, () -> {
                for (int i = 0; i <= particleCount; i++) {
                    double ratio = (double) i / particleCount;
                    Location particleLoc = from.clone().add(
                            (to.getX() - from.getX()) * ratio,
                            (to.getY() - from.getY()) * ratio,
                            (to.getZ() - from.getZ()) * ratio
                    );

                    if (from.getWorld() != null) {
                        from.getWorld().spawnParticle(
                                Particle.HAPPY_VILLAGER,
                                particleLoc, 1, 0, 0, 0, 0);
                    }
                }
            });
        } catch (Exception e) {
            MessageUtils.logError("Error creating attract particles: " + e.getMessage());
        }
    }

    @Override
    public boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        Long lastUse = playerCooldowns.get(playerId);

        if (lastUse == null) {
            return false;
        }

        return System.currentTimeMillis() - lastUse < COOLDOWN_MS;
    }

    private void setCooldown(Player player) {
        playerCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        // 清理过期的冷却记录以防止内存泄漏
        cleanupExpiredCooldowns();
    }

    /**
     * 清理过期的冷却记录
     */
    private static void cleanupExpiredCooldowns() {
        long currentTime = System.currentTimeMillis();
        playerCooldowns.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > COOLDOWN_MS * 2); // 保留2倍冷却时间以确保安全
    }

    private void sendCooldownMessage(Player player) {
        UUID playerId = player.getUniqueId();
        Long lastUse = playerCooldowns.get(playerId);

        if (lastUse != null) {
            long remainingMs = COOLDOWN_MS - (System.currentTimeMillis() - lastUse);
            double remainingSeconds = remainingMs / 1000.0;

            MessageUtils.sendLocalizedMessage(player, Messages.TOOLS_MAGNET_COOLDOWN,
                    "%time%", String.format("%.1f", remainingSeconds));
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public long getCooldownTime() {
        return COOLDOWN_MS;
    }
}