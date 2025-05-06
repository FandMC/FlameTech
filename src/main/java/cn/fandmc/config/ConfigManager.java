package cn.fandmc.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration lang;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        config = loadFile("config.yml");
        String langFile = "lang/" + config.getString("language") + ".yml";
        lang = loadFile(langFile);
        mergeDefault(lang, langFile);
        plugin.getLogger().info("当前使用语言:"+ config.getString("language"));
    }

    private FileConfiguration loadFile(String path) {
        File file = new File(plugin.getDataFolder(), path);
        try {
            if (!file.exists()) {
                if (plugin.getResource(path) != null) {
                    plugin.saveResource(path, false);
                } else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            }
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            //plugin.getLogger().warning("加载文件失败: " + path);
            return new YamlConfiguration();
        }
    }

    private void mergeDefault(FileConfiguration target, String path) {
        try (InputStream stream = plugin.getResource(path)) {
            if (stream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(stream, StandardCharsets.UTF_8)
                );
                for (String key : defaultConfig.getKeys(true)) {
                    if (!target.contains(key)) {
                        target.set(key, defaultConfig.get(key));
                    }
                }
            }
        } catch (Exception e) {
            //plugin.getLogger().warning("合并默认配置失败: " + e.getMessage());
        }
    }


    public String color(String path) {
        return lang.getString(path, "").replace('&', '§');
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public void save() {
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (Exception e) {
            plugin.getLogger().warning("保存配置失败");
        }
    }
}
