package cn.fandmc.commands;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class FlameTechCommand implements CommandExecutor {
    private final Main plugin;
    private final Map<String, BiFunction<CommandSender, String[], Boolean>> commands = new HashMap<>();

    public FlameTechCommand(Main plugin) {
        this.plugin = plugin;

        commands.put("help", this::handleHelp);
        commands.put("guide", this::handleGuide);
        commands.put("open", this::handleOpen);
        commands.put("reload", this::handleReload);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        BiFunction<CommandSender, String[], Boolean> handler = commands.get(subCommand);

        if (handler != null) {
            return handler.apply(sender, args);
        } else {
            sendHelp(sender);
            return true;
        }
    }


    private Boolean handleHelp(CommandSender sender, String[] args) {
        sendHelp(sender);
        return true;
    }

    private Boolean handleGuide(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
        } else {
            sender.sendMessage("只能对玩家执行此操作");
        }
        return true;
    }

    private Boolean handleOpen(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            String guiName = args.length > 1 ? args[1] : "main";
            GUIManager.openGUI(player, guiName);
        }
        return true;
    }

    private Boolean handleReload(CommandSender sender, String[] args) {
        plugin.reloadLang();
        sender.sendMessage(plugin.getConfigManager().getLang("command.reload.success"));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().getLang("command.help.title"));
        sender.sendMessage(plugin.getConfigManager().getLang("command.help.line1"));
        sender.sendMessage(plugin.getConfigManager().getLang("command.help.line2"));
        sender.sendMessage(plugin.getConfigManager().getLang("command.help.line3"));
        sender.sendMessage(plugin.getConfigManager().getLang("command.help.line4"));
    }
}
