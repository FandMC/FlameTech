package cn.fandmc.command.TabComplete;

import cn.fandmc.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class FlameTechCommand implements TabCompleter {
    private final Main plugin;
    private static final List<String> SUB_CMDS = Arrays.asList(
            "help",
            "guide",
            "open",
            "reload"
    );

    public FlameTechCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return args.length == 1 ? SUB_CMDS : List.of();
    }
}
