package cn.fandmc.unlock;

import cn.fandmc.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UnlockManager {
    private static UnlockManager instance;
    private final Main plugin;
    private final Map<UUID, PlayerUnlocks> playerUnlocks = new HashMap<>();
    private final Map<String, UnlockableItem> unlockableItems = new HashMap<>();
    private File unlocksFile;
    private FileConfiguration unlocksConfig;

    private UnlockManager(Main plugin) {
        this.plugin = plugin;
        loadUnlocksFile();
        loadUnlockableItems();
    }

    public static void init(Main plugin) {
        if (instance == null) {
            instance = new UnlockManager(plugin);
        }
    }

    public static UnlockManager getInstance() {
        return instance;
    }

    private void loadUnlocksFile() {
        unlocksFile = new File(plugin.getDataFolder(), "player_unlocks.yml");
        if (!unlocksFile.exists()) {
            try {
                unlocksFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        unlocksConfig = YamlConfiguration.loadConfiguration(unlocksFile);
        loadPlayerData();
    }

    private void loadUnlockableItems() {
        registerUnlockable(new UnlockableItem("multiblock.enhanced_crafting_table", 10));
    }

    public void registerUnlockable(UnlockableItem item) {
        unlockableItems.put(item.getId(), item);
    }

    public boolean isUnlocked(Player player, String itemId) {
        PlayerUnlocks unlocks = playerUnlocks.get(player.getUniqueId());
        if (unlocks == null) {
            unlocks = new PlayerUnlocks(player.getUniqueId());
            playerUnlocks.put(player.getUniqueId(), unlocks);
        }
        return unlocks.isUnlocked(itemId);
    }

    public boolean canUnlock(Player player, String itemId) {
        UnlockableItem item = unlockableItems.get(itemId);
        if (item == null) return true; // 如果没有注册解锁需求，默认已解锁

        return player.getLevel() >= item.getRequiredExp();
    }

    public UnlockResult unlock(Player player, String itemId) {
        if (isUnlocked(player, itemId)) {
            return new UnlockResult(false, "already_unlocked");
        }

        UnlockableItem item = unlockableItems.get(itemId);
        if (item == null) {
            return new UnlockResult(false, "item_not_found");
        }

        if (player.getLevel() < item.getRequiredExp()) {
            return new UnlockResult(false, "insufficient_exp", item.getRequiredExp());
        }

        player.setLevel(player.getLevel() - item.getRequiredExp());

        PlayerUnlocks unlocks = playerUnlocks.get(player.getUniqueId());
        if (unlocks == null) {
            unlocks = new PlayerUnlocks(player.getUniqueId());
            playerUnlocks.put(player.getUniqueId(), unlocks);
        }
        unlocks.unlock(itemId);

        savePlayerData(player.getUniqueId());

        return new UnlockResult(true, "success");
    }

    public int getRequiredExp(String itemId) {
        UnlockableItem item = unlockableItems.get(itemId);
        return item != null ? item.getRequiredExp() : 0;
    }

    private void loadPlayerData() {
        for (String uuidStr : unlocksConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            List<String> unlockedItems = unlocksConfig.getStringList(uuidStr);
            PlayerUnlocks unlocks = new PlayerUnlocks(uuid);
            for (String item : unlockedItems) {
                unlocks.unlock(item);
            }
            playerUnlocks.put(uuid, unlocks);
        }
    }

    private void savePlayerData(UUID uuid) {
        PlayerUnlocks unlocks = playerUnlocks.get(uuid);
        if (unlocks != null) {
            unlocksConfig.set(uuid.toString(), new ArrayList<>(unlocks.getUnlockedItems()));
            try {
                unlocksConfig.save(unlocksFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAllData() {
        for (UUID uuid : playerUnlocks.keySet()) {
            savePlayerData(uuid);
        }
    }

    public static class UnlockableItem {
        private final String id;
        private final int requiredExp;

        public UnlockableItem(String id, int requiredExp) {
            this.id = id;
            this.requiredExp = requiredExp;
        }

        public String getId() { return id; }
        public int getRequiredExp() { return requiredExp; }
    }

    public static class PlayerUnlocks {
        private final UUID playerId;
        private final Set<String> unlockedItems = new HashSet<>();

        public PlayerUnlocks(UUID playerId) {
            this.playerId = playerId;
        }

        public void unlock(String itemId) {
            unlockedItems.add(itemId);
        }

        public boolean isUnlocked(String itemId) {
            return unlockedItems.contains(itemId);
        }

        public Set<String> getUnlockedItems() {
            return new HashSet<>(unlockedItems);
        }
    }

    public static class UnlockResult {
        private final boolean success;
        private final String message;
        private final int requiredExp;

        public UnlockResult(boolean success, String message) {
            this(success, message, 0);
        }

        public UnlockResult(boolean success, String message, int requiredExp) {
            this.success = success;
            this.message = message;
            this.requiredExp = requiredExp;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getRequiredExp() { return requiredExp; }
    }
}