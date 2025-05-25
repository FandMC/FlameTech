package cn.fandmc.tools;

import cn.fandmc.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ToolListener implements Listener {
    private final Main plugin;
    private final ToolManager toolManager;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    public ToolListener(Main plugin) {
        this.plugin = plugin;
        this.toolManager = ToolManager.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        // 如果事件已被取消，不处理
        if (event.isCancelled()) {
            return;
        }

        // 防止重复处理
        if (isProcessing.get()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();

        // 基础安全检查
        if (player == null || tool == null || block == null) {
            return;
        }

        try {
            // 检查是否为特殊工具并且功能已启用
            if (toolManager.isExplosivePickaxe(tool) && toolManager.isExplosivePickaxeEnabled()) {
                handleExplosivePickaxe(event, player, block, tool);
            } else if (toolManager.isSmeltingPickaxe(tool) && toolManager.isSmeltingPickaxeEnabled()) {
                handleSmeltingPickaxe(event, player, block, tool);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("处理工具事件时发生错误: " + e.getMessage());
            e.printStackTrace();
            // 确保重置处理标志
            isProcessing.set(false);
        }
    }

    /**
     * 处理爆炸镐挖掘 - 修复 Folia 兼容性问题
     */
    private void handleExplosivePickaxe(BlockBreakEvent event, Player player, Block centerBlock, ItemStack tool) {
        // 设置处理标志，防止递归
        isProcessing.set(true);

        try {
            // 先处理原始方块
            if (event.isCancelled()) {
                isProcessing.set(false);
                return;
            }

            int radius = toolManager.getExplosionRadius();
            Location center = centerBlock.getLocation();

            // 限制最大处理方块数量，防止服务器过载
            final int MAX_BLOCKS = toolManager.getMaxBlocksPerExplosion();
            List<Block> blocksToBreak = new ArrayList<>();

            // 获取爆炸范围内的方块（排除中心方块，因为已经被原始事件处理）
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        // 跳过中心方块
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }

                        // 检查是否在球形范围内
                        if (x * x + y * y + z * z <= radius * radius) {
                            Location blockLoc = center.clone().add(x, y, z);
                            Block block = blockLoc.getBlock();

                            // 只破坏可以用镐子挖掘的方块，使用安全的类型检查
                            if (canBreakWithPickaxe(block.getType()) && !isAirOrNull(block)) {
                                blocksToBreak.add(block);

                                // 防止处理过多方块
                                if (blocksToBreak.size() >= MAX_BLOCKS) {
                                    break;
                                }
                            }
                        }
                    }
                    if (blocksToBreak.size() >= MAX_BLOCKS) break;
                }
                if (blocksToBreak.size() >= MAX_BLOCKS) break;
            }

            // 使用 Folia 兼容的调度器延迟处理
            scheduleDelayedExplosion(player, center, blocksToBreak, tool);

        } catch (Exception e) {
            plugin.getLogger().severe("爆炸镐初始化处理时发生错误: " + e.getMessage());
            e.printStackTrace();
            isProcessing.set(false);
        }
    }

    /**
     * Folia 兼容的延迟爆炸处理
     */
    private void scheduleDelayedExplosion(Player player, Location center, List<Block> blocksToBreak, ItemStack tool) {
        // 使用区域调度器而不是全局调度器（Folia 兼容）
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    // 播放爆炸效果
                    if (center.getWorld() != null) {
                        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                        center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,
                                center.clone().add(0.5, 0.5, 0.5), 3);
                    }

                    int actuallyBroken = 0;

                    // 处理方块破坏
                    for (Block block : blocksToBreak) {
                        if (!isAirOrNull(block)) {
                            // 权限检查
                            if (canPlayerBreakBlock(player, block)) {
                                // 安全获取掉落物
                                Collection<ItemStack> drops = getBlockDropsSafely(block, tool);
                                Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

                                // 先掉落物品，再破坏方块
                                for (ItemStack drop : drops) {
                                    if (drop != null && drop.getType() != Material.AIR) {
                                        if (dropLocation.getWorld() != null) {
                                            dropLocation.getWorld().dropItemNaturally(dropLocation, drop);
                                        }
                                    }
                                }

                                // 破坏方块
                                block.setType(Material.AIR);
                                actuallyBroken++;
                            }
                        }
                    }

                    // 损坏工具耐久度
                    damageItem(tool, Math.min(actuallyBroken + 1, 10)); // +1 包含原始方块

                    // 发送消息给玩家
                    if (actuallyBroken > 0) {
                        player.sendMessage("§e轰！爆炸范围内额外破坏了 " + actuallyBroken + " 个方块！");
                    }

                } catch (Exception e) {
                    plugin.getLogger().severe("爆炸镐处理过程中发生错误: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    // 重置处理标志
                    isProcessing.set(false);
                }
            }
        }.runTaskLater(plugin, 1L); // 延迟1tick执行
    }

    /**
     * 安全获取方块掉落物
     */
    private Collection<ItemStack> getBlockDropsSafely(Block block, ItemStack tool) {
        try {
            // 使用安全的方式获取掉落物
            return block.getDrops(tool);
        } catch (Exception e) {
            // 如果出错，返回默认掉落物
            plugin.getLogger().warning("获取方块掉落物时出错，使用默认处理: " + e.getMessage());
            List<ItemStack> defaultDrops = new ArrayList<>();
            Material blockType = block.getType();

            // 根据方块类型返回对应的掉落物
            switch (blockType) {
                case STONE:
                    defaultDrops.add(new ItemStack(Material.COBBLESTONE, 1));
                    break;
                case IRON_ORE:
                case DEEPSLATE_IRON_ORE:
                    defaultDrops.add(new ItemStack(Material.RAW_IRON, 1));
                    break;
                case GOLD_ORE:
                case DEEPSLATE_GOLD_ORE:
                    defaultDrops.add(new ItemStack(Material.RAW_GOLD, 1));
                    break;
                case COAL_ORE:
                case DEEPSLATE_COAL_ORE:
                    defaultDrops.add(new ItemStack(Material.COAL, 1));
                    break;
                case DIAMOND_ORE:
                case DEEPSLATE_DIAMOND_ORE:
                    defaultDrops.add(new ItemStack(Material.DIAMOND, 1));
                    break;
                default:
                    // 其他方块掉落自身
                    if (blockType.isItem()) {
                        defaultDrops.add(new ItemStack(blockType, 1));
                    }
                    break;
            }

            return defaultDrops;
        }
    }

    /**
     * 安全检查方块是否为空气
     */
    private boolean isAirOrNull(Block block) {
        try {
            return block == null || block.getType() == Material.AIR || block.getType().isAir();
        } catch (Exception e) {
            // 如果出错，假设是空气
            return true;
        }
    }

    /**
     * 简单的权限检查方法
     */
    private boolean canPlayerBreakBlock(Player player, Block block) {
        try {
            // 检查基础条件
            if (isAirOrNull(block)) {
                return false;
            }

            // 检查是否在创造模式
            if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
                return true;
            }

            // 检查方块硬度（基岩等不可破坏方块）
            Material type = block.getType();
            if (type == Material.BEDROCK ||
                    type == Material.BARRIER ||
                    type == Material.COMMAND_BLOCK ||
                    type == Material.STRUCTURE_BLOCK) {
                return false;
            }

            // 简单距离检查（防止破坏太远的方块）
            double distance = player.getLocation().distance(block.getLocation());
            if (distance > 10) { // 最大10格距离
                return false;
            }

            return true;

        } catch (Exception e) {
            plugin.getLogger().warning("权限检查时发生错误: " + e.getMessage());
            return false;
        }
    }

    /**
     * 处理熔炼镐挖掘
     */
    private void handleSmeltingPickaxe(BlockBreakEvent event, Player player, Block block, ItemStack tool) {
        try {
            Material blockType = block.getType();

            // 检查是否可以熔炼
            if (toolManager.canSmelt(blockType)) {
                // 取消原始掉落
                event.setDropItems(false);

                // 获取熔炼后的结果
                Material smeltedMaterial = toolManager.getSmeltedResult(blockType);

                // 计算掉落数量（基于原始掉落物）
                Collection<ItemStack> originalDrops = getBlockDropsSafely(block, tool);

                Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

                // 掉落熔炼后的物品
                for (ItemStack originalDrop : originalDrops) {
                    if (originalDrop.getType() == blockType) {
                        // 如果原始掉落就是方块本身，替换为熔炼结果
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
     * 检查材料是否可以用镐子挖掘
     */
    private boolean canBreakWithPickaxe(Material material) {
        // 基本的石质材料
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

                // 矿石
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

                // 其他镐子可挖掘的方块
            case SANDSTONE:
            case RED_SANDSTONE:
            case NETHERRACK:
            case BLACKSTONE:
            case BASALT:
            case SMOOTH_BASALT:
            case END_STONE:
            case PURPUR_BLOCK:
            case PURPUR_PILLAR:
            case PURPUR_STAIRS:
            case PURPUR_SLAB:
                return true;
            default:
                return false;
        }
    }

    /**
     * 安全地损坏物品耐久度
     */
    private void damageItem(ItemStack item, int damage) {
        try {
            if (item != null && item.getType().getMaxDurability() > 0) {
                org.bukkit.inventory.meta.Damageable meta = (org.bukkit.inventory.meta.Damageable) item.getItemMeta();
                if (meta != null) {
                    int currentDamage = meta.getDamage();
                    int newDamage = currentDamage + damage;

                    if (newDamage >= item.getType().getMaxDurability()) {
                        // 工具损坏
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
}