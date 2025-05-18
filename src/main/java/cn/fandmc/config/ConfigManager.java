package cn.fandmc.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigManager {
    private final JavaPlugin plugin;
    private YamlConfiguration config;
    private File configFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");

        saveDefaultConfig();

        this.config = YamlConfiguration.loadConfiguration(configFile);

        saveDefaultLang();
    }

    public void saveDefaultConfig() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
    }

    public void reloadConfig() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            reloadLang();
        } catch (Exception e) {
            plugin.getLogger().severe("配置文件重载失败: " + e.getMessage());
        }
    }

    private void saveDefaultLang() {
        String langName = config.getString("language");
        File langFile = new File(plugin.getDataFolder(), "lang/" + langName + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("lang/" + langName + ".yml", false);
        }
    }

    public String getLang(String key) {
        String langName = config.getString("language"); // 默认语言
        File langFile = new File(plugin.getDataFolder(), "lang/" + langName + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("lang/" + langName + ".yml", false);
        }

        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
        String message = lang.getString(key, "&c未找到语言键: " + key);

        return ChatColor.translateAlternateColorCodes('&', message);
    }


    private void reloadLang() {
        String langName = Objects.requireNonNull(config.getString("language")).toLowerCase();
        plugin.saveResource("lang/" + langName + ".yml", false);
    }
}
