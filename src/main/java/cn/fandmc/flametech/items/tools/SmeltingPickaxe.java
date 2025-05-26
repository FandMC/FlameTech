package cn.fandmc.flametech.items.tools;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ConfigKeys;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.items.base.SpecialTool;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.utils.ItemUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 熔炼镐工具
 */
public class SmeltingPickaxe extends SpecialTool {

    private static final Map<Material, Material> SMELTING_MAP = new HashMap<>();

    static {
        // 初始化熔炼映射
        SMELTING_MAP.put(Material.IRON_ORE, Material.IRON_INGOT);
        SMELTING_MAP.put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
        SMELTING_MAP.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        SMELTING_MAP.put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
        SMELTING_MAP.put(Material.NETHER_GOLD_ORE, Material.GOLD_INGOT);
        SMELTING_MAP.put(Material.COPPER_ORE, Material.COPPER_INGOT);
        SMELTING_MAP.put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
        SMELTING_MAP.put(Material.RAW_IRON, Material.IRON_INGOT);
        SMELTING_MAP.put(Material.RAW_GOLD, Material.GOLD_INGOT);
        SMELTING_MAP.put(Material.RAW_COPPER, Material.COPPER_INGOT);

        // 非矿物熔炼
        SMELTING_MAP.put(Material.SAND, Material.GLASS);
        SMELTING_MAP.put(Material.COBBLESTONE, Material.STONE);
        SMELTING_MAP.put(Material.STONE, Material.SMOOTH_STONE);
    }

    public SmeltingPickaxe(Main plugin) {
        super(plugin, ItemKeys.ID_SMELTING_PICKAXE,
                plugin.getConfigManager().getSafeLang(Messages.ITEMS_SMELTING_PICKAXE_NAME, "熔炼镐"));
    }

    @Override
    public ItemStack createItem() {
        String displayName = plugin.getConfigManager().getLang(Messages.ITEMS_SMELTING_PICKAXE_NAME);

        // 获取 lore 列表
        List<String> lore = plugin.getConfigManager().getStringList(Messages.ITEMS_SMELTING_PICKAXE_LORE);

        return new ItemBuilder(Material.IRON_PICKAXE)
                .displayName(displayName)
                .lore(lore)
                .nbt(nbtKey, "true")
                .build();
    }

    @Override
    public void handleBlockBreak(BlockBreakEvent event, Player player, Block block, ItemStack tool) {
        if (!canUse(player, block, tool)) {
            return;
        }

        try {
            Material blockType = block.getType();
            Material smeltedMaterial = SMELTING_MAP.get(blockType);

            if (smeltedMaterial != null) {
                processSmeltedDrop(event, player, block, tool, smeltedMaterial);
            }

            // 正常消耗耐久度
            ItemUtils.damageItem(tool, 1);

        } catch (Exception e) {
            MessageUtils.logError("Error in smelting pickaxe: " + e.getMessage());
        }
    }

    private void processSmeltedDrop(BlockBreakEvent event, Player player, Block block, ItemStack tool, Material smeltedMaterial) {
        // 取消原始掉落
        event.setDropItems(false);

        // 获取原始掉落物
        Collection<ItemStack> originalDrops = getBlockDrops(block, tool);
        Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

        // 处理掉落物
        for (ItemStack originalDrop : originalDrops) {
            ItemStack finalDrop = shouldSmeltDrop(originalDrop) ?
                    new ItemStack(smeltedMaterial, originalDrop.getAmount()) :
                    originalDrop;

            if (dropLocation.getWorld() != null) {
                dropLocation.getWorld().dropItemNaturally(dropLocation, finalDrop);
            }
        }

        // 播放效果
        playSmeltingEffects(player, block.getLocation());
    }

    private boolean shouldSmeltDrop(ItemStack drop) {
        if (ItemUtils.isAirOrNull(drop)) {
            return false;
        }

        Material type = drop.getType();
        return SMELTING_MAP.containsKey(type) || isRawMaterial(type);
    }

    private boolean isRawMaterial(Material material) {
        return material == Material.RAW_IRON ||
                material == Material.RAW_GOLD ||
                material == Material.RAW_COPPER;
    }

    private Collection<ItemStack> getBlockDrops(Block block, ItemStack tool) {
        try {
            return block.getDrops(tool);
        } catch (Exception e) {
            MessageUtils.logWarning("Failed to get block drops for smelting: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private void playSmeltingEffects(Player player, Location location) {
        if (!shouldPlayEffects()) {
            return;
        }

        try {
            // 音效
            if (shouldPlaySounds()) {
                player.playSound(location, Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5f, 1.0f);
            }

            // 粒子效果
            if (shouldPlayParticles() && location.getWorld() != null) {
                location.getWorld().spawnParticle(Particle.FLAME,
                        location.clone().add(0.5, 0.5, 0.5), 5, 0.2, 0.2, 0.2, 0.01);
            }
        } catch (Exception e) {
            MessageUtils.logWarning("Failed to play smelting effects: " + e.getMessage());
        }
    }

    @Override
    public boolean isEnabled() {
        return ValidationUtils.getConfigBoolean(ConfigKeys.TOOLS_SMELTING_ENABLED, true);
    }

    private boolean shouldPlayEffects() {
        return ValidationUtils.getConfigBoolean(ConfigKeys.TOOLS_SMELTING_PARTICLE_EFFECTS, true);
    }

    private boolean shouldPlaySounds() {
        return ValidationUtils.getConfigBoolean(ConfigKeys.TOOLS_SMELTING_SOUND_EFFECTS, true);
    }

    private boolean shouldPlayParticles() {
        return ValidationUtils.getConfigBoolean(ConfigKeys.TOOLS_SMELTING_PARTICLE_EFFECTS, true);
    }
}