package cn.fandmc.command;

import cn.fandmc.Loader.BukkitLoader;
import cn.fandmc.Loader.FoliaLoader;
import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.item.Book;
import cn.fandmc.logger.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import cn.fandmc.config.ConfigManager;

import java.util.Arrays;

public class FlameTechCommands implements CommandExecutor{
    private final Logger logger;
    private final Main plugin;
    private final ConfigManager config;

    public FlameTechCommands(Logger logger, Main plugin, ConfigManager config) {
        this.logger = logger;
        this.plugin = plugin;
        this.config = config;
    }

    private String[] HELP_MENU = {
            getConfig("Commands.HelpMenu"),
            "&e/flametech help &7- " + getConfig("Commands.help.help"),
            "&e/flametech guide &7- " + getConfig("Commands.help.guide"),
            "&e/flametech open &7- " + getConfig("Commands.help.open"),
            "&e/flametech reload &7- " + getConfig("Commands.help.reload")
    };
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help" -> sendHelp(sender);
            case "guide" -> handleGuide(sender);
            case "open" -> handleOpen(sender);
            case "reload" -> reload(config, sender);
            default -> Logger.send(sender, getConfig("Commands.default"));
        }
        return true;
    }

    private void handleOpen(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            Logger.send(sender, getConfig("Commands.Console"));
            return;
        }
        GUI.open(player);
    }

    private void reload(ConfigManager config,CommandSender sender) {
        config.save();
        config.reload();
        Logger.send(sender, getConfig("Commands.reload.done"));
    }

    private void sendHelp(CommandSender sender) {
        Arrays.stream(HELP_MENU).forEach(line -> Logger.send(sender, line));
    }
    private void handleGuide(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            Logger.send(sender, getConfig("Commands.Console"));
            return;
        }

        try {
            Book.giveGuideBook(player);
            Logger.send(sender, getConfig("Commands.giveBook"));
        } catch (Exception e) {
            Logger.error("生成指南书失败: " + e.getMessage(),plugin);
        }
    }
    private String getConfig(String config){
        return Main.getconfig().color(config);
    }
}
