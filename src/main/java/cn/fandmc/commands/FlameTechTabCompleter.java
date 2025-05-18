package cn.fandmc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class FlameTechTabCompleter implements TabCompleter {

    private static final List<String> MAIN_COMMANDS = List.of("help", "guide", "open", "reload");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], MAIN_COMMANDS, completions);
        }

        return completions;
    }
}
