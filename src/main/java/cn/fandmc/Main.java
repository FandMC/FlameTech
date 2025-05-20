package cn.fandmc;

import cn.fandmc.config.ConfigManager;
import cn.fandmc.commands.*;
import cn.fandmc.gui.BookClickListener;
import cn.fandmc.gui.GUIListener;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.MainGUI;
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
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        instance = this;
        Objects.requireNonNull(getCommand("FlameTech")).setExecutor(new FlameTechCommand(this));
        Objects.requireNonNull(getCommand("FlameTech")).setTabCompleter(new FlameTechTabCompleter());
        try {
            this.configManager = new ConfigManager(this);
            GUIManager.init(this);
            new MainGUI(this);
        } catch (Exception e) {
            getLogger().severe("配置初始化失败: " + e.getMessage());
            return;
        }
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

