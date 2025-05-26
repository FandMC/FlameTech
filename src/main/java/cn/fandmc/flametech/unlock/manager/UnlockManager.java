package cn.fandmc.flametech.unlock.manager;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.ItemKeys;
import cn.fandmc.flametech.unlock.data.PlayerUnlocks;
import cn.fandmc.flametech.unlock.data.UnlockResult;
import cn.fandmc.flametech.unlock.data.UnlockableItem;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解锁管理器
 */
public class UnlockManager {

    private final Main plugin;
    private final Map<UUID, PlayerUnlocks> playerUnlocks = new ConcurrentHashMap<>();
    private final Map<String, UnlockableItem> unlockableItems = new ConcurrentHashMap<>();

    private File unlocksFile;
    private FileConfiguration unlocksConfig;

    public UnlockManager(Main plugin) {
        this.plugin = plugin;
        initializeDataFile();
    }

    private void initializeDataFile() {
        unlocksFile = new File(plugin.getDataFolder(), "player_unlocks.yml");
        if (!unlocksFile.exists()) {
            try {
                unlocksFile.createNewFile();
            } catch (IOException e) {
                MessageUtils.logError("Failed to create player unlocks file: " + e.getMessage());
            }
        }
        unlocksConfig = YamlConfiguration.loadConfiguration(unlocksFile);
        loadPlayerData();
    }

    /**
     * 注册默认可解锁物品
     */
    public void registerDefaultUnlockables() {
        try {
            // 注册多方块解锁
            registerUnlockable(new UnlockableItem(ItemKeys.MULTIBLOCK_ENHANCED_CRAFTING_TABLE, 10, "multiblock", "增强工作台"));

            // 注册配方解锁
            registerUnlockable(new UnlockableItem(ItemKeys.RECIPE_EXPLOSIVE_PICKAXE, 15, "recipe", "爆炸镐配方"));
            registerUnlockable(new UnlockableItem(ItemKeys.RECIPE_SMELTING_PICKAXE, 20, "recipe", "熔炼镐配方"));

            MessageUtils.logInfo("注册了 " + unlockableItems.size() + " 个可解锁物品");

        } catch (Exception e) {
            MessageUtils.logError("注册默认可解锁物品失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 注册可解锁物品
     */
    public void registerUnlockable(UnlockableItem item) {
        if (item == null) {
            MessageUtils.logWarning("尝试注册空可解锁物品");
            return;
        }

        String itemId = item.getItemId();
        if (unlockableItems.containsKey(itemId)) {
            MessageUtils.logWarning("ID为 '" + itemId + "' 的可解锁物品已存在");
            return;
        }

        unlockableItems.put(itemId, item);
        //MessageUtils.logInfo("注册可解锁物品: " + itemId + " (经验: " + item.getRequiredExp() + ")");
    }

    /**
     * 检查玩家是否已解锁指定物品
     */
    public boolean isUnlocked(Player player, String itemId) {
        if (!ValidationUtils.isValidPlayer(player) || !ValidationUtils.isValidString(itemId)) {
            return false;
        }

        PlayerUnlocks unlocks = getOrCreatePlayerUnlocks(player.getUniqueId());
        return unlocks.isUnlocked(itemId);
    }

    /**
     * 检查玩家是否可以解锁指定物品
     */
    public boolean canUnlock(Player player, String itemId) {
        if (!ValidationUtils.isValidPlayer(player) || !ValidationUtils.isValidString(itemId)) {
            return false;
        }

        UnlockableItem item = unlockableItems.get(itemId);
        if (item == null) {
            return true; // 如果没有注册解锁需求，默认可解锁
        }

        return player.getLevel() >= item.getRequiredExp();
    }

    /**
     * 尝试解锁物品
     */
    public UnlockResult unlock(Player player, String itemId) {
        if (!ValidationUtils.isValidPlayer(player)) {
            return UnlockResult.playerNotFound();
        }

        if (!ValidationUtils.isValidString(itemId)) {
            return UnlockResult.itemNotFound(itemId);
        }

        // 检查是否已解锁
        if (isUnlocked(player, itemId)) {
            return UnlockResult.alreadyUnlocked(itemId);
        }

        UnlockableItem item = unlockableItems.get(itemId);
        if (item == null) {
            // 如果没有注册，直接解锁
            PlayerUnlocks unlocks = getOrCreatePlayerUnlocks(player.getUniqueId());
            unlocks.unlock(itemId);
            savePlayerDataAsync(player.getUniqueId());
            return UnlockResult.success(itemId);
        }

        // 检查经验是否足够
        if (player.getLevel() < item.getRequiredExp()) {
            return UnlockResult.insufficientExp(item.getRequiredExp(), itemId);
        }

        try {
            // 扣除经验
            player.setLevel(player.getLevel() - item.getRequiredExp());

            // 添加解锁
            PlayerUnlocks unlocks = getOrCreatePlayerUnlocks(player.getUniqueId());
            unlocks.unlock(itemId);

            // 异步保存数据
            savePlayerDataAsync(player.getUniqueId());

            return UnlockResult.success(itemId);

        } catch (Exception e) {
            MessageUtils.logError("Error during unlock process: " + e.getMessage());
            return UnlockResult.error("unlock_error");
        }
    }

    /**
     * 获取物品的解锁经验需求
     */
    public int getRequiredExp(String itemId) {
        UnlockableItem item = unlockableItems.get(itemId);
        return item != null ? item.getRequiredExp() : 0;
    }

    /**
     * 获取或创建玩家解锁数据
     */
    private PlayerUnlocks getOrCreatePlayerUnlocks(UUID playerId) {
        return playerUnlocks.computeIfAbsent(playerId, PlayerUnlocks::new);
    }

    /**
     * 加载所有玩家数据
     */
    private void loadPlayerData() {
        if (unlocksConfig == null) {
            return;
        }

        try {
            for (String uuidStr : unlocksConfig.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                List<String> unlockedItems = unlocksConfig.getStringList(uuidStr);

                PlayerUnlocks unlocks = new PlayerUnlocks(uuid);
                unlocks.addUnlockedItems(new HashSet<>(unlockedItems));

                playerUnlocks.put(uuid, unlocks);
            }

            MessageUtils.logInfo("加载了 " + playerUnlocks.size() + " 个玩家的数据");

        } catch (Exception e) {
            MessageUtils.logError("Error loading player unlock data: " + e.getMessage());
        }
    }

    /**
     * 保存指定玩家的数据
     */
    private void savePlayerData(UUID playerId) {
        PlayerUnlocks unlocks = playerUnlocks.get(playerId);
        if (unlocks != null && unlocksConfig != null) {
            unlocksConfig.set(playerId.toString(), new ArrayList<>(unlocks.getUnlockedItems()));
            try {
                unlocksConfig.save(unlocksFile);
            } catch (IOException e) {
                MessageUtils.logError("Failed to save player unlock data: " + e.getMessage());
            }
        }
    }

    /**
     * 异步保存玩家数据
     */
    private void savePlayerDataAsync(UUID playerId) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> savePlayerData(playerId));
    }

    /**
     * 保存所有玩家数据
     */
    public void saveAllData() {
        try {
            for (UUID playerId : playerUnlocks.keySet()) {
                savePlayerData(playerId);
            }
            MessageUtils.logInfo("Saved unlock data for all players");
        } catch (Exception e) {
            MessageUtils.logError("Error saving all player unlock data: " + e.getMessage());
        }
    }

    /**
     * 获取玩家的解锁统计
     */
    public Map<String, Object> getPlayerStatistics(Player player) {
        if (!ValidationUtils.isValidPlayer(player)) {
            return new HashMap<>();
        }

        PlayerUnlocks unlocks = playerUnlocks.get(player.getUniqueId());
        if (unlocks == null) {
            return new HashMap<>();
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total_unlocked", unlocks.getUnlockedCount());
        stats.put("total_available", unlockableItems.size());

        // 按类别统计
        Map<String, Integer> categoryStats = new HashMap<>();
        for (UnlockableItem item : unlockableItems.values()) {
            String category = item.getCategory();
            categoryStats.put(category, categoryStats.getOrDefault(category, 0) + 1);
        }
        stats.put("by_category", categoryStats);

        return stats;
    }

    /**
     * 重置玩家的所有解锁
     */
    public boolean resetPlayerUnlocks(UUID playerId) {
        PlayerUnlocks unlocks = playerUnlocks.get(playerId);
        if (unlocks != null) {
            unlocks.clearAllUnlocks();
            savePlayerDataAsync(playerId);
            return true;
        }
        return false;
    }

    /**
     * 获取所有可解锁物品
     */
    public Map<String, UnlockableItem> getAllUnlockableItems() {
        return new HashMap<>(unlockableItems);
    }

    /**
     * 重新加载解锁管理器
     */
    public void reload() {
        playerUnlocks.clear();
        unlockableItems.clear();

        initializeDataFile();
        registerDefaultUnlockables();

        MessageUtils.logInfo("Reloaded unlock manager");
    }
}