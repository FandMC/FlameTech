package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class MessageUtils {

    /**
     * 发送带颜色代码的消息
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender != null && message != null) {
            sender.sendMessage(colorize(message));
        }
    }

    /**
     * 发送多行消息
     */
    public static void sendMessages(CommandSender sender, List<String> messages) {
        if (sender != null && messages != null) {
            messages.forEach(message -> sendMessage(sender, message));
        }
    }

    /**
     * 发送国际化消息
     */
    public static void sendLocalizedMessage(CommandSender sender, String key) {
        String message = Main.getInstance().getConfigManager().getLang(key);
        sendMessage(sender, message);
    }

    /**
     * 发送带参数替换的国际化消息
     */
    public static void sendLocalizedMessage(CommandSender sender, String key, String... replacements) {
        String message = Main.getInstance().getConfigManager().getLang(key);

        // 替换参数 %param1%, %param2% 等
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }

        sendMessage(sender, message);
    }

    /**
     * 转换颜色代码
     */
    public static String colorize(String text) {
        return text != null ? ChatColor.translateAlternateColorCodes('&', text) : "";
    }

    /**
     * 转换颜色代码列表
     */
    public static List<String> colorize(List<String> texts) {
        return texts != null ?
                texts.stream().map(MessageUtils::colorize).collect(Collectors.toList()) :
                List.of();
    }

    /**
     * 向玩家发送动作栏消息
     */
    public static void sendActionBar(Player player, String message) {
        if (player != null && message != null) {
            try {
                player.sendActionBar(colorize(message));
            } catch (Exception e) {
                // 如果动作栏不支持，则发送普通消息
                sendMessage(player, message);
            }
        }
    }

    /**
     * 向玩家发送标题
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player != null) {
            try {
                player.sendTitle(
                        title != null ? colorize(title) : "",
                        subtitle != null ? colorize(subtitle) : "",
                        fadeIn, stay, fadeOut
                );
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("Failed to send title to player: " + e.getMessage());
            }
        }
    }

    /**
     * 发送本地化的消息列表
     * @param sender 消息接收者
     * @param key 语言文件中的key
     * @param replacements 替换参数（可选）
     */
    public static void sendLocalizedMessageList(CommandSender sender, String key, String... replacements) {
        try {
            List<String> messages = Main.getInstance().getConfigManager().getStringList(key);

            if (messages != null && !messages.isEmpty()) {
                for (String message : messages) {
                    // 应用参数替换
                    String processedMessage = applyReplacements(message, replacements);
                    sendMessage(sender, processedMessage);
                }
            } else {
                // 如果列表为空或不存在，发送错误提示
                sendMessage(sender, "&c未找到帮助信息: " + key);
            }
        } catch (Exception e) {
            logError("Error sending localized message list for key: " + key + " - " + e.getMessage());
            sendMessage(sender, "&c发送消息时发生错误");
        }
    }

    /**
     * 发送本地化的消息列表（带标题）
     * @param sender 消息接收者
     * @param titleKey 标题key
     * @param listKey 列表key
     * @param replacements 替换参数（可选）
     */
    public static void sendLocalizedMessageListWithTitle(CommandSender sender, String titleKey, String listKey, String... replacements) {
        // 先发送标题
        sendLocalizedMessage(sender, titleKey, replacements);
        // 再发送列表
        sendLocalizedMessageList(sender, listKey, replacements);
    }

    /**
     * 应用参数替换的辅助方法
     */
    private static String applyReplacements(String message, String... replacements) {
        if (replacements.length == 0) {
            return message;
        }

        String result = message;
        for (int i = 0; i < replacements.length - 1; i += 2) {
            if (i + 1 < replacements.length) {
                String placeholder = replacements[i];
                String replacement = replacements[i + 1];
                result = result.replace(placeholder, replacement);
            }
        }
        return result;
    }

    /**
     * 记录带颜色的控制台消息
     */
    public static void logInfo(String message) {
        Main.getInstance().getLogger().info(stripColors(message));
    }

    /**
     * 记录警告消息
     */
    public static void logWarning(String message) {
        Main.getInstance().getLogger().warning(stripColors(message));
    }

    /**
     * 记录错误消息
     */
    public static void logError(String message) {
        Main.getInstance().getLogger().severe(stripColors(message));
    }

    /**
     * 移除颜色代码
     */
    public static String stripColors(String text) {
        return text != null ? ChatColor.stripColor(colorize(text)) : "";
    }

    /**
     * 格式化玩家名称
     */
    public static String formatPlayerName(Player player) {
        return player != null ? player.getName() : "Unknown";
    }

    /**
     * 检查字符串是否为空或null
     */
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private MessageUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}