package cn.fandmc.flametech.config;

import cn.fandmc.flametech.constants.ConfigKeys;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 配置管理器 - 统一管理插件配置和语言文件
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    private FileConfiguration langConfig;
    private String currentLanguage;
    private File langFile;

    // 缓存系统
    private final ConcurrentMap<String, String> langCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<String>> langListCache = new ConcurrentHashMap<>();
    private long lastReloadTime;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.lastReloadTime = System.currentTimeMillis();

        initializeConfig();
        initializeLanguage();
    }

    private void initializeConfig() {
        this.configFile = new File(plugin.getDataFolder(), "config.yml");

        // 创建默认配置文件
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
        MessageUtils.logInfo("Configuration file loaded successfully");
    }

    private void initializeLanguage() {
        // 获取语言设置
        this.currentLanguage = config.getString(ConfigKeys.LANGUAGE, "zh_cn");

        // 创建语言文件夹
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // 加载语言文件
        loadLanguageFile();
    }

    private void loadLanguageFile() {
        this.langFile = new File(plugin.getDataFolder(), "lang/" + currentLanguage + ".yml");

        // 创建默认语言文件
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + currentLanguage + ".yml", false);
        }

        this.langConfig = YamlConfiguration.loadConfiguration(langFile);

        // 清空缓存
        clearCache();

        MessageUtils.logInfo("Language file loaded: " + currentLanguage);
    }

    /**
     * 重载配置
     */
    public void reloadConfig() {
        try {
            // 重载主配置
            if (configFile.exists()) {
                config = YamlConfiguration.loadConfiguration(configFile);
            }

            // 检查语言是否变更
            String newLanguage = config.getString(ConfigKeys.LANGUAGE, "zh_cn");
            if (!newLanguage.equals(currentLanguage)) {
                this.currentLanguage = newLanguage;
                loadLanguageFile();
            } else {
                // 重载当前语言文件
                reloadLanguage();
            }

            lastReloadTime = System.currentTimeMillis();
            MessageUtils.logInfo("重载完成");

        } catch (Exception e) {
            MessageUtils.logError("Failed to reload configuration: " + e.getMessage());
            throw new RuntimeException("Configuration reload failed", e);
        }
    }

    /**
     * 重载语言文件
     */
    private void reloadLanguage() {
        if (langFile.exists()) {
            langConfig = YamlConfiguration.loadConfiguration(langFile);
            clearCache();
        }
    }

    /**
     * 获取本地化字符串
     */
    public String getLang(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        // 尝试从缓存获取
        String cached = langCache.get(path);
        if (cached != null) {
            return cached;
        }

        // 从配置文件获取
        String message = getLanguageString(path);

        // 处理颜色代码
        message = ChatColor.translateAlternateColorCodes('&', message);

        // 存入缓存
        langCache.put(path, message);

        return message;
    }

    /**
     * 获取本地化字符串列表
     */
    public List<String> getStringList(String path) {
        if (path == null || path.isEmpty()) {
            return new ArrayList<>();
        }

        // 尝试从缓存获取
        List<String> cached = langListCache.get(path);
        if (cached != null) {
            return new ArrayList<>(cached);
        }

        // 从配置文件获取
        List<String> result = new ArrayList<>();

        if (langConfig != null) {
            List<?> list = langConfig.getList(path);
            if (list != null) {
                for (Object obj : list) {
                    if (obj instanceof String str) {
                        result.add(ChatColor.translateAlternateColorCodes('&', str));
                    }
                }
            }
        }

        // 如果没有找到，返回默认值
        if (result.isEmpty()) {
            result.add(ChatColor.translateAlternateColorCodes('&', "&c未找到语言列表: " + path));
        }

        // 存入缓存
        langListCache.put(path, new ArrayList<>(result));

        return result;
    }

    private String getLanguageString(String path) {
        if (langConfig == null) {
            return ChatColor.translateAlternateColorCodes('&', "&c未找到语言配置文件");
        }

        String message = langConfig.getString(path);
        if (message == null) {
            return ChatColor.translateAlternateColorCodes('&', "&c未找到语言键: " + path);
        }

        return message;
    }

    /**
     * 获取带参数替换的本地化字符串
     */
    public String getLang(String path, String... replacements) {
        String message = getLang(path);

        // 替换参数
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }

        return message;
    }

    /**
     * 清空缓存
     */
    private void clearCache() {
        langCache.clear();
        langListCache.clear();
    }

    /**
     * 保存默认语言文件
     */
    public void saveDefaultLanguage() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        File defaultLangFile = new File(langFolder, currentLanguage + ".yml");
        if (!defaultLangFile.exists()) {
            plugin.saveResource("lang/" + currentLanguage + ".yml", false);
        }

        loadLanguageFile();
    }

    /**
     * 设置配置值
     */
    public void setConfigValue(String path, Object value) {
        config.set(path, value);
        saveConfig();
    }

    /**
     * 获取配置值
     */
    public Object getConfigValue(String path, Object defaultValue) {
        return config.get(path, defaultValue);
    }

    /**
     * 保存配置文件
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            MessageUtils.logError("Failed to save configuration: " + e.getMessage());
        }
    }

    /**
     * 获取统计信息
     */
    public ConfigStatistics getStatistics() {
        return new ConfigStatistics(
                langCache.size(),
                langListCache.size(),
                lastReloadTime,
                currentLanguage,
                configFile.length(),
                langFile != null ? langFile.length() : 0
        );
    }

    // Getter方法
    public FileConfiguration getConfig() { return config; }
    public FileConfiguration getLangConfig() { return langConfig; }
    public String getCurrentLanguage() { return currentLanguage; }
    public long getLastReloadTime() { return lastReloadTime; }

    /**
     * 配置统计信息
     */
    public static class ConfigStatistics {
        private final int langCacheSize;
        private final int langListCacheSize;
        private final long lastReloadTime;
        private final String currentLanguage;
        private final long configFileSize;
        private final long langFileSize;

        public ConfigStatistics(int langCacheSize, int langListCacheSize, long lastReloadTime,
                                String currentLanguage, long configFileSize, long langFileSize) {
            this.langCacheSize = langCacheSize;
            this.langListCacheSize = langListCacheSize;
            this.lastReloadTime = lastReloadTime;
            this.currentLanguage = currentLanguage;
            this.configFileSize = configFileSize;
            this.langFileSize = langFileSize;
        }

        // Getter方法
        public int getLangCacheSize() { return langCacheSize; }
        public int getLangListCacheSize() { return langListCacheSize; }
        public long getLastReloadTime() { return lastReloadTime; }
        public String getCurrentLanguage() { return currentLanguage; }
        public long getConfigFileSize() { return configFileSize; }
        public long getLangFileSize() { return langFileSize; }
    }
}