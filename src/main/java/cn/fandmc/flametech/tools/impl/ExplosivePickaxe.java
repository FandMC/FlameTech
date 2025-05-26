package cn.fandmc.flametech.tools.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.tools.SpecialTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExplosivePickaxe extends SpecialTool {
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    public ExplosivePickaxe(Main plugin) {
        super(plugin, "explosive_pickaxe", "爆炸镐");
    }

    @Override
    public void handleBlockBreak(BlockBreakEvent event, Player player, Block block, ItemStack tool) {
        // 防止重复处理
        if (isProcessing.get()) {
            return;
        }

        handleExplosivePickaxe(event, player, block, tool);
    }

    private void handleExplosivePickaxe(BlockBreakEvent event, Player player, Block centerBlock, ItemStack tool) {
        // 设置处理标志，防止递归
        isProcessing.set(true);

        try {
            // 先处理原始方块
            if (event.isCancelled()) {
                isProcessing.set(false);
                return;
            }

            int radius = getExplosionRadius();
            Location center = centerBlock.getLocation();

            // 限制最大处理方块数量，防止服务器过载
            final int MAX_BLOCKS = getMaxBlocksPerExplosion();
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

                            // 只破坏可以用镐子挖掘的方块
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

            // 使用延迟处理
            scheduleDelayedExplosion(player, center, blocksToBreak, tool);

        } catch (Exception e) {
            plugin.getLogger().severe("爆炸镐初始化处理时发生错误: " + e.getMessage());
            e.printStackTrace();
            isProcessing.set(false);
        }
    }

    private void scheduleDelayedExplosion(Player player, Location center, List<Block> blocksToBreak, ItemStack tool) {
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

    private int getExplosionRadius() {
        try {
            int radius = plugin.getConfig().getInt("tools.explosive_pickaxe.explosion_radius", 2);
            return Math.max(1, Math.min(3, radius));
        } catch (Exception e) {
            plugin.getLogger().warning("读取爆炸半径配置失败，使用默认值2: " + e.getMessage());
            return 2;
        }
    }

    private int getMaxBlocksPerExplosion() {
        try {
            int maxBlocks = plugin.getConfig().getInt("tools.explosive_pickaxe.max_blocks", 50);
            return Math.max(5, Math.min(100, maxBlocks));
        } catch (Exception e) {
            plugin.getLogger().warning("读取最大方块数配置失败，使用默认值50: " + e.getMessage());
            return 50;
        }
    }

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

    private boolean isAirOrNull(Block block) {
        try {
            return block == null || block.getType() == Material.AIR || block.getType().isAir();
        } catch (Exception e) {
            return true;
        }
    }

    private boolean canPlayerBreakBlock(Player player, Block block) {
        try {
            if (isAirOrNull(block)) {
                return false;
            }

            if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
                return true;
            }

            Material type = block.getType();
            if (type == Material.BEDROCK ||
                    type == Material.BARRIER ||
                    type == Material.COMMAND_BLOCK ||
                    type == Material.STRUCTURE_BLOCK) {
                return false;
            }

            double distance = player.getLocation().distance(block.getLocation());
            if (distance > 10) {
                return false;
            }

            return true;

        } catch (Exception e) {
            plugin.getLogger().warning("权限检查时发生错误: " + e.getMessage());
            return false;
        }
    }

    private Collection<ItemStack> getBlockDropsSafely(Block block, ItemStack tool) {
        try {
            return block.getDrops(tool);
        } catch (Exception e) {
            plugin.getLogger().warning("获取方块掉落物时出错，使用默认处理: " + e.getMessage());
            List<ItemStack> defaultDrops = new ArrayList<>();
            Material blockType = block.getType();

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
                    if (blockType.isItem()) {
                        defaultDrops.add(new ItemStack(blockType, 1));
                    }
                    break;
            }

            return defaultDrops;
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
            return plugin.getConfig().getBoolean("tools.explosive_pickaxe.enabled", true);
        } catch (Exception e) {
            return true;
        }
    }
}