package cn.fandmc;

import cn.fandmc.recipe.RecipeGUI;
import cn.fandmc.Loader.*;
import cn.fandmc.config.ConfigManager;
import cn.fandmc.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import cn.fandmc.util.UpdateChecker;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {
    private Logger logger;
    private java.util.logging.Logger logger1;
    private static Main instance;
    private String serverName = Bukkit.getServer().getName();
    private static ConfigManager config;
    private RecipeGUI recipeGUI;
    @Override
    public void onEnable() {
        getLogger().info("\n"+
                "  ______ _                   _______        _     \n" +
                " |  ____| |                 |__   __|      | |    \n" +
                " | |__  | | __ _ _ __ ___   ___| | ___  ___| |__  \n" +
                " |  __| | |/ _` | '_ ` _ \\ / _ \\ |/ _ \\/ __| '_ \\ \n" +
                " | |    | | (_| | | | | | |  __/ |  __/ (__| | | |\n" +
                " |_|    |_|\\__,_|_| |_| |_|\\___|_|\\___|\\___|_| |_|\n"+
                "\n热力科技 - " + serverName
        );

        logger1 = java.util.logging.Logger.getLogger("FlameTech");
        logger = new Logger(this);
        recipeGUI = new RecipeGUI(this);
        config = new ConfigManager(this);
        instance = this;

        //config.reload();
        validateEnvironment();
        initPlatform();
    }

    @Override
    public void onDisable() {
    }

    private void validateEnvironment() {
        if (Runtime.version().feature() < 21) {
            throw new UnsupportedOperationException("需要Java21或更高版本");
        }
    }

    private void initPlatform() {
        if(isFolia()){
            getLogger().info("检测到Folia服务端，启用Folia适配模式");
            new FoliaLoader(this, logger, config);
        }else{
            getLogger().info("检测到标准Bukkit服务端，启用传统模式");
            new BukkitLoader(this, logger, config);
        }

    }
    public static Main getPlugin() {
        return instance;
    }

    public static ConfigManager getconfig(){
        return config;
    }

    public void checkUpdate(CommandSender sender) {
        if (!getConfig().getBoolean("update-checker.enabled", true)) {
            if (sender != null) {
                sender.sendMessage("§c更新检查功能已被禁用");
            }
            return;
        }
        Runnable checkTask = () -> {
            try {
                String currentVersion = getDescription().getVersion();
                UpdateChecker checker = new UpdateChecker(
                        "FandMC/FlameTech",
                        currentVersion,
                        getLogger()
                );

                String finalMessage = getString(checker, currentVersion);
                Bukkit.getScheduler().runTask(this, () -> {
                    sender.sendMessage("§8[§cFlameTech§8] §r" + finalMessage);
                });
            } catch (Exception e) {
                String errorMsg = "更新检查失败: " + e.getMessage();
                getLogger().warning(errorMsg);
                if (sender != null) {
                    sender.sendMessage("§c" + errorMsg);
                }
            }
        };

        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().run(this, task -> checkTask.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this, checkTask);
        }
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static @NotNull String getString(UpdateChecker checker, String currentVersion) {
        String latestVersion = checker.getLatestVersion();
        String finalMessage;
        if (latestVersion == null) {
            finalMessage = "§e无法连接到更新服务器";
        } else if (currentVersion.equals(latestVersion)) {
            finalMessage = "§a已运行最新版本 (" + currentVersion + ")";
        } else {
            finalMessage = "§c发现新版本 " + latestVersion + "! 当前版本: " + currentVersion + "\n"
                    + "§b下载地址: https://github.com/你的GitHub用户名/仓库名/releases/latest";
        }
        return finalMessage;
    }
}

