package cn.fandmc.logger;

import cn.fandmc.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public record Logger(Main plugin) {
    private static final String PREFIX = ChatColor.DARK_AQUA + "FlameTech >> " + ChatColor.RESET;

    public static void log(String message, Main plugin) {
        plugin.getLogger().info(PREFIX + message);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + message.replace("&", "§"));
    }

    public static void error(String message, Main plugin) {
        plugin.getLogger().warning("[ERROR]:" + message);
    }
}
