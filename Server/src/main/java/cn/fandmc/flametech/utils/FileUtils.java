package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 */
public final class FileUtils {

    /**
     * 安全地创建文件
     */
    public static boolean createFileIfNotExists(File file) {
        if (file == null) {
            return false;
        }

        try {
            if (!file.exists()) {
                // 确保父目录存在
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }

                return file.createNewFile();
            }
            return true;
        } catch (IOException e) {
            MessageUtils.logError("Failed to create file: " + file.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 安全地创建目录
     */
    public static boolean createDirectoryIfNotExists(File directory) {
        if (directory == null) {
            return false;
        }

        try {
            if (!directory.exists()) {
                return directory.mkdirs();
            }
            return true;
        } catch (Exception e) {
            MessageUtils.logError("Failed to create directory: " + directory.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 读取文件内容为字符串
     */
    public static String readFileToString(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            MessageUtils.logError("Failed to read file: " + file.getPath() + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 读取文件内容为行列表
     */
    public static List<String> readFileToLines(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return new ArrayList<>();
        }

        try {
            return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            MessageUtils.logError("Failed to read file lines: " + file.getPath() + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 将字符串写入文件
     */
    public static boolean writeStringToFile(File file, String content) {
        if (file == null || content == null) {
            return false;
        }

        try {
            // 确保父目录存在
            createDirectoryIfNotExists(file.getParentFile());

            Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            MessageUtils.logError("Failed to write file: " + file.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 将行列表写入文件
     */
    public static boolean writeLinesToFile(File file, List<String> lines) {
        if (file == null || lines == null) {
            return false;
        }

        try {
            // 确保父目录存在
            createDirectoryIfNotExists(file.getParentFile());

            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            MessageUtils.logError("Failed to write file lines: " + file.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 安全地删除文件
     */
    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            return true;
        }

        try {
            return file.delete();
        } catch (Exception e) {
            MessageUtils.logError("Failed to delete file: " + file.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(File source, File destination) {
        if (source == null || destination == null || !source.exists()) {
            return false;
        }

        try {
            // 确保目标目录存在
            createDirectoryIfNotExists(destination.getParentFile());

            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            MessageUtils.logError("Failed to copy file: " + source.getPath() + " to " + destination.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取文件大小（字节）
     */
    public static long getFileSize(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return 0;
        }

        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            MessageUtils.logError("Failed to get file size: " + file.getPath() + " - " + e.getMessage());
            return 0;
        }
    }

    /**
     * 检查文件是否可读
     */
    public static boolean isReadable(File file) {
        return file != null && file.exists() && file.canRead();
    }

    /**
     * 检查文件是否可写
     */
    public static boolean isWritable(File file) {
        return file != null && file.exists() && file.canWrite();
    }

    /**
     * 获取插件数据目录下的文件
     */
    public static File getPluginFile(String relativePath) {
        return new File(Main.getInstance().getDataFolder(), relativePath);
    }

    /**
     * 创建备份文件
     */
    public static boolean createBackup(File originalFile) {
        if (originalFile == null || !originalFile.exists()) {
            return false;
        }

        String backupName = originalFile.getName() + ".backup." + System.currentTimeMillis();
        File backupFile = new File(originalFile.getParent(), backupName);

        return copyFile(originalFile, backupFile);
    }

    /**
     * 清理旧的备份文件（保留最新的几个）
     */
    public static void cleanupBackups(File directory, String filePrefix, int keepCount) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }

        try {
            File[] backupFiles = directory.listFiles((dir, name) ->
                    name.startsWith(filePrefix + ".backup."));

            if (backupFiles != null && backupFiles.length > keepCount) {
                // 按修改时间排序
                java.util.Arrays.sort(backupFiles,
                        (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

                // 删除多余的备份
                for (int i = keepCount; i < backupFiles.length; i++) {
                    deleteFile(backupFiles[i]);
                }
            }
        } catch (Exception e) {
            MessageUtils.logError("Failed to cleanup backups: " + e.getMessage());
        }
    }

    private FileUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}