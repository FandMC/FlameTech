package cn.fandmc.flametech.items.tools;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ConfigKeys;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.items.base.SpecialTool;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.utils.FoliaUtils;
import cn.fandmc.flametech.utils.ItemUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExplosivePickaxe extends SpecialTool {

    private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    public ExplosivePickaxe(Main plugin) {
        super(plugin, ItemKeys.ID_EXPLOSIVE_PICKAXE, "爆炸镐");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(Material.IRON_PICKAXE)
                .displayName("&c&l爆炸镐")
                .lore(
                        "&7一把充满破坏力的镐子",
                        "&7能够炸毁大片区域的方块",
                        "&c小心使用！",
                        "",
                        "&e功能:",
                        "&7• 破坏" + getExplosionRadius() + "格半径内的方块",
                        "&7• 可以破坏任何可破坏的方块",
                        "&7• 产生爆炸效果和音效",
                        "&7• 消耗额外耐久度",
                        "",
                        "&c[FlameTech 工具]"
                )
                .nbt(nbtKey, "true")
                .build();
    }

    @Override
    public void handleBlockBreak(BlockBreakEvent event, Player player, Block block, ItemStack tool) {
        if (!canUse(player, block, tool) || isProcessing.get()) {
            return;
        }

        isProcessing.set(true);

        try {
            int radius = getExplosionRadius();
            int maxBlocks = getMaxBlocksPerExplosion();
            Location center = block.getLocation();

            List<Block> blocksToBreak = findBlocksToBreak(center, radius, maxBlocks);

            if (!blocksToBreak.isEmpty()) {
                FoliaUtils.runTaskLater(center, () -> {
                    try {
                        performExplosion(player, center, blocksToBreak, tool);
                    } finally {
                        isProcessing.set(false);
                    }
                }, 1L);
            } else {
                isProcessing.set(false);
            }

        } catch (Exception e) {
            MessageUtils.logError("Error in explosive pickaxe: " + e.getMessage());
            isProcessing.set(false);
        }
    }

    private List<Block> findBlocksToBreak(Location center, int radius, int maxBlocks) {
        List<Block> blocks = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // 跳过中心方块（已被原始事件处理）
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    // 检查是否在球形范围内
                    if (x * x + y * y + z * z <= radius * radius) {
                        Location blockLoc = center.clone().add(x, y, z);
                        Block candidateBlock = blockLoc.getBlock();

                        if (canBreakBlock(candidateBlock)) {
                            blocks.add(candidateBlock);

                            if (blocks.size() >= maxBlocks) {
                                return blocks;
                            }
                        }
                    }
                }
            }
        }

        return blocks;
    }

    private boolean canBreakBlock(Block block) {
        if (block == null) return false;

        Material type = block.getType();

        // 排除空气和液体
        if (type.isAir() || isLiquid(type)) {
            return false;
        }

        // 排除绝对不能破坏的方块
        if (isUnbreakableBlock(type)) {
            return false;
        }

        // 检查材料是否是有效的物品类型
        if (!type.isItem()) {
            return false;
        }

        // 额外检查：确保可以用镐子破坏
        return canBreakWithPickaxe(type);
    }

    private boolean isLiquid(Material type) {
        switch (type) {
            case WATER:
            case LAVA:
            case BUBBLE_COLUMN:
                return true;
            default:
                return false;
        }
    }

    private boolean canBreakWithPickaxe(Material type) {
        switch (type) {
            // 基本的石质材料
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

                // 人造方块
            case CRAFTING_TABLE:
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
            case DISPENSER:
            case DROPPER:
            case OBSERVER:
            case PISTON:
            case STICKY_PISTON:
            case REDSTONE_BLOCK:
            case IRON_BLOCK:
            case GOLD_BLOCK:
            case DIAMOND_BLOCK:
            case EMERALD_BLOCK:
            case COAL_BLOCK:
            case COPPER_BLOCK:
            case EXPOSED_COPPER:
            case WEATHERED_COPPER:
            case OXIDIZED_COPPER:
            case CUT_COPPER:
            case EXPOSED_CUT_COPPER:
            case WEATHERED_CUT_COPPER:
            case OXIDIZED_CUT_COPPER:
            case WAXED_COPPER_BLOCK:
            case WAXED_EXPOSED_COPPER:
            case WAXED_WEATHERED_COPPER:
            case WAXED_OXIDIZED_COPPER:
            case WAXED_CUT_COPPER:
            case WAXED_EXPOSED_CUT_COPPER:
            case WAXED_WEATHERED_CUT_COPPER:
            case WAXED_OXIDIZED_CUT_COPPER:
                return true;
            default:
                return false;
        }
    }

    private boolean isUnbreakableBlock(Material type) {
        switch (type) {
            // 绝对保护的方块
            case BEDROCK:
            case BARRIER:
            case COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case REPEATING_COMMAND_BLOCK:
            case STRUCTURE_BLOCK:
            case JIGSAW:
            case STRUCTURE_VOID:
            case END_PORTAL:
            case END_PORTAL_FRAME:
            case NETHER_PORTAL:
                return true;
            default:
                return false;
        }
    }

    private void performExplosion(Player player, Location center, List<Block> blocksToBreak, ItemStack tool) {
        // 播放爆炸效果
        playExplosionEffects(center);

        int brokenCount = 0;

        for (Block block : blocksToBreak) {
            if (canPlayerBreakBlock(player, block)) {
                try {
                    // 安全地获取掉落物
                    Collection<ItemStack> drops = getBlockDropsSafely(block, tool);
                    Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

                    // 掉落物品
                    for (ItemStack drop : drops) {
                        if (!ItemUtils.isAirOrNull(drop) && dropLocation.getWorld() != null) {
                            dropLocation.getWorld().dropItemNaturally(dropLocation, drop);
                        }
                    }

                    // 破坏方块
                    block.setType(Material.AIR);
                    brokenCount++;

                } catch (Exception e) {
                    MessageUtils.logWarning("Failed to break block at " + block.getLocation() + ": " + e.getMessage());
                    try {
                        block.setType(Material.AIR);
                        brokenCount++;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // 损坏工具
        int damageAmount = Math.min(brokenCount + 1, getMaxDurabilityDamage());
        ItemUtils.damageItem(tool, damageAmount);

        // 发送消息
        if (brokenCount > 0) {
            MessageUtils.sendLocalizedMessage(player,
                    cn.fandmc.flametech.constants.Messages.TOOLS_EXPLOSIVE_EXPLOSION,
                    "%blocks%", String.valueOf(brokenCount));
        }
    }

    private void playExplosionEffects(Location center) {
        if (center.getWorld() != null) {
            try {
                center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,
                        center.clone().add(0.5, 0.5, 0.5), 3);
            } catch (Exception e) {
                MessageUtils.logWarning("Failed to play explosion effects: " + e.getMessage());
            }
        }
    }

    private boolean canPlayerBreakBlock(Player player, Block block) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        if (isUnbreakableBlock(block.getType())) {
            return false;
        }

        // 检查距离限制
        double distance = player.getLocation().distance(block.getLocation());
        if (distance > 10.0) {
            return false;
        }

        return ValidationUtils.canPlayerBreakAtDistance(player, block.getLocation(), 10.0);
    }

    private Collection<ItemStack> getBlockDropsSafely(Block block, ItemStack tool) {
        try {
            // 在正确的线程上下文中获取掉落物
            return block.getDrops(tool);
        } catch (Exception e) {
            MessageUtils.logWarning("Failed to get block drops for " + block.getType() + ": " + e.getMessage());
            return getDefaultDrops(block.getType());
        }
    }

    private Collection<ItemStack> getDefaultDrops(Material blockType) {
        List<ItemStack> drops = new ArrayList<>();

        // 检查材料是否是有效的物品类型
        if (!blockType.isItem()) {
            return drops; // 返回空列表
        }

        // 根据方块类型提供默认掉落物
        switch (blockType) {
            case STONE:
            case DEEPSLATE:
                drops.add(new ItemStack(Material.COBBLESTONE, 1));
                break;
            case COBBLED_DEEPSLATE:
                drops.add(new ItemStack(Material.COBBLED_DEEPSLATE, 1));
                break;
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                drops.add(new ItemStack(Material.RAW_IRON, 1));
                break;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case NETHER_GOLD_ORE:
                drops.add(new ItemStack(Material.RAW_GOLD, 1));
                break;
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                drops.add(new ItemStack(Material.RAW_COPPER, 1));
                break;
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                drops.add(new ItemStack(Material.COAL, 1));
                break;
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                drops.add(new ItemStack(Material.DIAMOND, 1));
                break;
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                drops.add(new ItemStack(Material.EMERALD, 1));
                break;
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                drops.add(new ItemStack(Material.LAPIS_LAZULI, 1));
                break;
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                drops.add(new ItemStack(Material.REDSTONE, 4));
                break;
            case NETHER_QUARTZ_ORE:
                drops.add(new ItemStack(Material.QUARTZ, 1));
                break;
            // 木头类
            case OAK_LOG:
            case BIRCH_LOG:
            case SPRUCE_LOG:
            case JUNGLE_LOG:
            case ACACIA_LOG:
            case DARK_OAK_LOG:
            case MANGROVE_LOG:
            case CHERRY_LOG:
            case WARPED_STEM:
            case CRIMSON_STEM:
            case STRIPPED_OAK_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_SPRUCE_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_MANGROVE_LOG:
            case STRIPPED_CHERRY_LOG:
            case STRIPPED_WARPED_STEM:
            case STRIPPED_CRIMSON_STEM:
                drops.add(new ItemStack(blockType, 1));
                break;
            // 叶子类（有概率掉落）
            case OAK_LEAVES:
            case BIRCH_LEAVES:
            case SPRUCE_LEAVES:
            case JUNGLE_LEAVES:
            case ACACIA_LEAVES:
            case DARK_OAK_LEAVES:
            case MANGROVE_LEAVES:
            case CHERRY_LEAVES:
            case WARPED_WART_BLOCK:
            case NETHER_WART_BLOCK:
                // 叶子有概率掉落自身或幼苗
                if (Math.random() < 0.05) { // 5%概率掉落叶子
                    drops.add(new ItemStack(blockType, 1));
                }
                if (Math.random() < 0.05) { // 5%概率掉落幼苗
                    Material sapling = getSaplingFromLeaves(blockType);
                    if (sapling != null) {
                        drops.add(new ItemStack(sapling, 1));
                    }
                }
                break;
            // 泥土类
            case DIRT:
            case COARSE_DIRT:
            case ROOTED_DIRT:
                drops.add(new ItemStack(Material.DIRT, 1));
                break;
            case GRASS_BLOCK:
                drops.add(new ItemStack(Material.DIRT, 1));
                break;
            case SAND:
                drops.add(new ItemStack(Material.SAND, 1));
                break;
            case GRAVEL:
                // 砂砾有概率掉落燧石
                if (Math.random() < 0.1) {
                    drops.add(new ItemStack(Material.FLINT, 1));
                } else {
                    drops.add(new ItemStack(Material.GRAVEL, 1));
                }
                break;
            default:
                // 对于其他方块，如果是物品，就掉落自身
                if (blockType.isItem()) {
                    drops.add(new ItemStack(blockType, 1));
                }
                break;
        }

        return drops;
    }

    private Material getSaplingFromLeaves(Material leaves) {
        switch (leaves) {
            case OAK_LEAVES: return Material.OAK_SAPLING;
            case BIRCH_LEAVES: return Material.BIRCH_SAPLING;
            case SPRUCE_LEAVES: return Material.SPRUCE_SAPLING;
            case JUNGLE_LEAVES: return Material.JUNGLE_SAPLING;
            case ACACIA_LEAVES: return Material.ACACIA_SAPLING;
            case DARK_OAK_LEAVES: return Material.DARK_OAK_SAPLING;
            case MANGROVE_LEAVES: return Material.MANGROVE_PROPAGULE;
            case CHERRY_LEAVES: return Material.CHERRY_SAPLING;
            default: return null;
        }
    }

    @Override
    public boolean isEnabled() {
        return ValidationUtils.getConfigBoolean(ConfigKeys.TOOLS_EXPLOSIVE_ENABLED, true);
    }

    private int getExplosionRadius() {
        return ValidationUtils.getConfigInt(ConfigKeys.TOOLS_EXPLOSIVE_RADIUS, 2, 1, 3);
    }

    private int getMaxBlocksPerExplosion() {
        return ValidationUtils.getConfigInt(ConfigKeys.TOOLS_EXPLOSIVE_MAX_BLOCKS, 50, 5, 100);
    }

    private int getMaxDurabilityDamage() {
        return ValidationUtils.getConfigInt(ConfigKeys.TOOLS_EXPLOSIVE_MAX_DURABILITY_DAMAGE, 10, 1, 20);
    }
}