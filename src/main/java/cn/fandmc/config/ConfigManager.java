package cn.fandmc.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    private FileConfiguration lang;
    private String langName;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.langName = "zh_cn";

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);

        String langFileName = this.config.getString("language");
        this.langName = langFileName;
        File langFile = new File(plugin.getDataFolder(), "lang/" + langFileName + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("lang/" + langFileName + ".yml", false);
        }

        this.lang = YamlConfiguration.loadConfiguration(langFile);
    }

    public void reloadConfig() {
        try {
            if (configFile.exists()) {
                config = YamlConfiguration.loadConfiguration(configFile);
            }

            reloadLang();
        } catch (Exception e) {
            plugin.getLogger().severe("配置文件重载失败: " + e.getMessage());
        }
    }

    private void reloadLang() {
        File langFile = new File(plugin.getDataFolder(), "lang/" + langName + ".yml");
        if (langFile.exists()) {
            lang = YamlConfiguration.loadConfiguration(langFile);
        }
    }

    public FileConfiguration getLangConfig() {
        return lang;
    }

    public List<String> getStringList(String path) {
        List<String> result = new ArrayList<>();
        if (lang == null) return result;

        List<?> list = lang.getList(path);
        if (list == null) return result;

        for (Object obj : list) {
            if (obj instanceof String str) {
                result.add(ChatColor.translateAlternateColorCodes('&', str));
            }
        }

        return result;
    }

    public String getLang(String path) {
        if (lang == null) {
            return ChatColor.translateAlternateColorCodes('&', "&c未找到语言配置项: " + path);
        }

        String message = lang.getString(path, "&c未找到语言键: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void saveDefaultLang() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // 如果 lang.yml 不存在，就复制一份
        File langFile = new File(plugin.getDataFolder(), "lang/" + langName + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + langName + ".yml", false);
        }

        // 确保 lang 被加载
        lang = YamlConfiguration.loadConfiguration(langFile);
    }
}
