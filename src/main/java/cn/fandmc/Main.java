package cn.fandmc;

import cn.fandmc.config.ConfigManager;
import cn.fandmc.commands.*;
import cn.fandmc.gui.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {

    private String serverName = Bukkit.getServer().getName();
    private static Main instance;
    private ConfigManager configManager;
    @Override
    public void onEnable() {
        getLogger().info("""
                
                  ______ _                   _______        _    \s
                 |  ____| |                 |__   __|      | |   \s
                 | |__  | | __ _ _ __ ___   ___| | ___  ___| |__ \s
                 |  __| | |/ _` | '_ ` _ \\ / _ \\ |/ _ \\/ __| '_ \\\s
                 | |    | | (_| | | | | | |  __/ |  __/ (__| | | |
                 |_|    |_|\\__,_|_| |_| |_|\\___|_|\\___|\\___|_| |_|
                """
        );
        getLogger().info(
            String.format("热力科技 - %s [%s]",
            getDescription().getVersion(),
            serverName
        ));
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            instance = this;
            GUIManager.init(this);
            this.configManager = new ConfigManager(this);
            configManager.saveDefaultConfig(); // 会触发语言文件保存
        } catch (Exception e) {
            getLogger().severe("配置初始化失败: " + e.getMessage());
            return;
        }
        Objects.requireNonNull(getCommand("FlameTech")).setExecutor(new FlameTechCommand(this));
        Objects.requireNonNull(getCommand("FlameTech")).setTabCompleter(new FlameTechTabCompleter());
    }

    @Override
    public void onDisable() {
    }

    public static Main getInstance() {
        return instance;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
    public void reloadLang() {
        configManager.reloadConfig();
    }
}

