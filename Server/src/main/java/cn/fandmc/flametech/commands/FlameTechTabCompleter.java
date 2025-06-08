package cn.fandmc.flametech.commands;

import cn.fandmc.flametech.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FlameTech命令Tab补全器
 */
public class FlameTechTabCompleter implements TabCompleter {

    private static final List<String> MAIN_COMMANDS = Arrays.asList(
            "help", "guide", "open", "reload", "info"
    );

    private static final List<String> GUI_NAMES = Arrays.asList(
            "main", "basic_machines", "tools"
    );

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 第一级命令补全
            List<String> availableCommands = new ArrayList<>();

            for (String cmd : MAIN_COMMANDS) {
                if (hasPermissionForCommand(sender, cmd)) {
                    availableCommands.add(cmd);
                }
            }

            StringUtil.copyPartialMatches(args[0], availableCommands, completions);

        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            if ("open".equals(subCommand) && sender.hasPermission(Permissions.COMMAND_OPEN)) {
                StringUtil.copyPartialMatches(args[1], GUI_NAMES, completions);
            }
        }

        return completions;
    }

    private boolean hasPermissionForCommand(CommandSender sender, String command) {
        return switch (command) {
            case "help" -> sender.hasPermission(Permissions.COMMAND_HELP);
            case "guide" -> sender.hasPermission(Permissions.COMMAND_GUIDE);
            case "open" -> sender.hasPermission(Permissions.COMMAND_OPEN);
            case "reload" -> sender.hasPermission(Permissions.COMMAND_RELOAD);
            case "info" -> sender.hasPermission(Permissions.ADMIN);
            default -> false;
        };
    }
}