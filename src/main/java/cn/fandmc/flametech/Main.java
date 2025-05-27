package cn.fandmc.flametech;

import cn.fandmc.flametech.commands.FlameTechCommand;
import cn.fandmc.flametech.commands.FlameTechTabCompleter;
import cn.fandmc.flametech.config.ConfigManager;
import cn.fandmc.flametech.constants.ConfigKeys;
import cn.fandmc.flametech.gui.manager.GUIManager;
import cn.fandmc.flametech.items.manager.ItemManager;
import cn.fandmc.flametech.listeners.BlockBreakListener;
import cn.fandmc.flametech.listeners.PlayerInteractListener;
import cn.fandmc.flametech.materials.manager.MaterialManager;
import cn.fandmc.flametech.multiblock.manager.MultiblockManager;
import cn.fandmc.flametech.recipes.manager.RecipeManager;
import cn.fandmc.flametech.unlock.manager.UnlockManager;
import cn.fandmc.flametech.utils.FoliaUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private ItemManager itemManager;
    private RecipeManager recipeManager;
    private MultiblockManager multiblockManager;
    private UnlockManager unlockManager;
    private GUIManager guiManager;
    private MaterialManager materialManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            printStartupBanner();

            FoliaUtils.initialize(this);

            initializeManagers();

            registerCommands();

            registerListeners();

            registerDefaultContent();
        } catch (Exception e) {
            MessageUtils.logError("Failed to enable FlameTech plugin: " + e.getMessage());
            e.printStackTrace();
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            // 保存数据
            if (unlockManager != null) {
                unlockManager.saveAllData();
            }

        } catch (Exception e) {
            MessageUtils.logError("Error during plugin disable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printStartupBanner() {
        getLogger().info("""
                
                  ______ _                   _______        _    \s
                 |  ____| |                 |__   __|      | |   \s
                 | |__  | | __ _ _ __ ___   ___| | ___  ___| |__ \s
                 |  __| | |/ _` | '_ ` _ \\ / _ \\ |/ _ \\/ __| '_ \\\s
                 | |    | | (_| | | | | | |  __/ |  __/ (__| | | |
                 |_|    |_|\\__,_|_| |_| |_|\\___|_|\\___|\\___|_| |_|
                 热力科技
                """);

        MessageUtils.logInfo("FlameTech v" + getDescription().getVersion() + " by " +
                String.join(", ", getDescription().getAuthors()));
        MessageUtils.logInfo("运行于: " + getServer().getName() + " " + getServer().getVersion());
    }

    private void initializeManagers() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.configManager = new ConfigManager(this);
        saveDefaultConfig();

        this.itemManager = new ItemManager(this);
        this.materialManager = new MaterialManager(this);
        this.multiblockManager = new MultiblockManager(this);
        this.recipeManager = new RecipeManager(this);

        // UnlockManager 必须在 RecipeManager 和 MultiblockManager 之后初始化
        // 因为它需要从这些管理器中加载解锁信息
        this.unlockManager = new UnlockManager(this);

        this.guiManager = new GUIManager(this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("flametech")).setExecutor(new FlameTechCommand(this));
        Objects.requireNonNull(getCommand("flametech")).setTabCompleter(new FlameTechTabCompleter());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
    }

    private void registerDefaultContent() {
        // 注册顺序很重要
        // 1. 先注册物品
        itemManager.registerDefaultItems();

        // 2. 注册材料
        materialManager.registerDefaultMaterials();

        // 3. 再注册多方块结构
        multiblockManager.registerDefaultStructures();

        // 4. 然后注册配方
        recipeManager.registerDefaultRecipes();

        // 5. 最后注册解锁项（依赖于上面的内容）
        unlockManager.registerDefaultUnlockables();

        // 6. 注册GUI
        guiManager.registerDefaultGUIs();
    }

    /**
     * 重载插件配置
     */
    public void reloadPluginConfig() {
        try {
            reloadConfig();
            configManager.reloadConfig();

            // 重载各个管理器
            itemManager.reload();
            materialManager.reload();
            multiblockManager.reload();
            recipeManager.reload();

            // UnlockManager 的重载必须在其他管理器之后
            // 因为它需要重新加载解锁信息
            unlockManager.reload();

            guiManager.reload();

        } catch (Exception e) {
            MessageUtils.logError("Failed to reload configuration: " + e.getMessage());
            throw e;
        }
    }

    // Getter methods
    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public MaterialManager getMaterialManager() {
        return materialManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public MultiblockManager getMultiblockManager() {
        return multiblockManager;
    }

    public UnlockManager getUnlockManager() {
        return unlockManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public boolean isDebugMode() {
        return getConfig().getBoolean(ConfigKeys.DEBUG_ENABLED, false);
    }

    public String getPluginVersion() {
        return getDescription().getVersion();
    }
}