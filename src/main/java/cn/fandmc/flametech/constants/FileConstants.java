package cn.fandmc.flametech.constants;

/**
 * 文件和路径相关常量
 */
public final class FileConstants {

    // 配置文件
    public static final String CONFIG_FILE = "config.yml";

    // 语言文件
    public static final String LANG_FOLDER = "lang";
    public static final String LANG_FILE_EXTENSION = ".yml";
    public static final String DEFAULT_LANGUAGE = "zh_cn";

    // 路径分隔符
    public static final String PATH_SEPARATOR = "/";

    // 构建语言文件路径的工具方法
    public static String getLangFilePath(String language) {
        return LANG_FOLDER + PATH_SEPARATOR + language + LANG_FILE_EXTENSION;
    }

    private FileConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}