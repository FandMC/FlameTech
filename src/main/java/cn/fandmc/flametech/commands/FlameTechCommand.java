package cn.fandmc.flametech.commands;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.constants.Permissions;
import cn.fandmc.flametech.utils.BookUtils;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * FlameTech主命令执行器
 */
public class FlameTechCommand implements CommandExecutor {

    private final Main plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public FlameTechCommand(Main plugin) {
        this.plugin = plugin;
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("help", new HelpCommand());
        subCommands.put("guide", new GuideCommand());
        subCommands.put("open", new OpenCommand());
        subCommands.put("reload", new ReloadCommand());
        subCommands.put("info", new InfoCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand != null) {
            return subCommand.execute(sender, args);
        } else {
            sendHelp(sender);
            return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        // 使用列表格式发送帮助消息
        MessageUtils.sendLocalizedMessageList(sender, Messages.COMMAND_HELP_MESSAGES);
    }

    // 内部接口
    private interface SubCommand {
        boolean execute(CommandSender sender, String[] args);
    }

    // 帮助命令
    private class HelpCommand implements SubCommand {
        @Override
        public boolean execute(CommandSender sender, String[] args) {
            if (!sender.hasPermission(Permissions.COMMAND_HELP)) {
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_ERROR_NO_PERMISSION);
                return true;
            }

            sendHelp(sender);
            return true;
        }
    }

    // 指南书命令
    private class GuideCommand implements SubCommand {
        @Override
        public boolean execute(CommandSender sender, String[] args) {
            if (!sender.hasPermission(Permissions.COMMAND_GUIDE)) {
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_ERROR_NO_PERMISSION);
                return true;
            }

            if (!(sender instanceof Player player)) {
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_GUIDE_ONLY_PLAYER);
                return true;
            }

            try {
                player.getInventory().addItem(BookUtils.createGuideBook());

                String bookName = plugin.getConfigManager().getLang(Messages.GUIDE_BOOK_DISPLAY_NAME);
                MessageUtils.sendLocalizedMessage(player, Messages.COMMAND_GUIDE_SUCCESS,
                        "%book%", bookName);

                return true;
            } catch (Exception e) {
                MessageUtils.logError("Error giving guide book: " + e.getMessage());
                MessageUtils.sendLocalizedMessage(player, Messages.COMMAND_ERROR_GENERIC);
                return true;
            }
        }
    }

    // 打开GUI命令
    private class OpenCommand implements SubCommand {
        @Override
        public boolean execute(CommandSender sender, String[] args) {
            if (!sender.hasPermission(Permissions.COMMAND_OPEN)) {
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_ERROR_NO_PERMISSION);
                return true;
            }

            if (!(sender instanceof Player player)) {
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_GUIDE_ONLY_PLAYER);
                return true;
            }

            String guiName = args.length > 1 ? args[1] : "main";

            try {
                boolean success = plugin.getGuiManager().openGUI(player, guiName);
                if (!success) {
                    MessageUtils.sendLocalizedMessage(player, Messages.COMMAND_OPEN_INVALID_GUI,
                            "%gui%", guiName);
                }
                return true;
            } catch (Exception e) {
                MessageUtils.logError("Error opening GUI: " + e.getMessage());
                MessageUtils.sendLocalizedMessage(player, Messages.COMMAND_OPEN_ERROR);
                return true;
            }
        }
    }

    // 重载命令
    private class ReloadCommand implements SubCommand {
        @Override
        public boolean execute(CommandSender sender, String[] args) {
            if (!sender.hasPermission(Permissions.COMMAND_RELOAD)) {
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_ERROR_NO_PERMISSION);
                return true;
            }

            try {
                plugin.reloadPluginConfig();
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_RELOAD_SUCCESS);
                return true;
            } catch (Exception e) {
                MessageUtils.logError("Error reloading plugin: " + e.getMessage());
                MessageUtils.sendMessage(sender, "&c重载插件时发生错误: " + e.getMessage());
                return true;
            }
        }
    }

    private class InfoCommand implements SubCommand {
        @Override
        public boolean execute(CommandSender sender, String[] args) {
            if (!sender.hasPermission(Permissions.ADMIN)) {
                // 非管理员只显示解锁进度
                if (sender instanceof Player player) {
                    Map<String, Object> playerStats = plugin.getUnlockManager().getPlayerStatistics(player);
                    MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_PLAYER_PROGRESS,
                            "%unlocked%", String.valueOf(playerStats.getOrDefault("total_unlocked", 0)),
                            "%total%", String.valueOf(playerStats.getOrDefault("total_available", 0)));
                }
                return true;
            }

            try {
                // 管理员信息
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_TITLE);

                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_VERSION,
                        "%version%", plugin.getPluginVersion());

                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_ITEMS,
                        "%count%", String.valueOf(plugin.getItemManager().getRegisteredItemCount()));

                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_RECIPES,
                        "%count%", String.valueOf(plugin.getRecipeManager().getRecipeCount()));

                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_MULTIBLOCKS,
                        "%count%", String.valueOf(plugin.getMultiblockManager().getRegisteredStructureCount()));

                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_GUIS,
                        "%count%", String.valueOf(plugin.getGuiManager().getRegisteredGUICount()));

                String debugStatus = plugin.isDebugMode() ?
                        plugin.getConfigManager().getLang(Messages.COMMON_STATUS_ENABLED) :
                        plugin.getConfigManager().getLang(Messages.COMMON_STATUS_DISABLED);

                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_DEBUG,
                        "%status%", debugStatus);

                // 如果是玩家，显示解锁进度
                if (sender instanceof Player player) {
                    Map<String, Object> playerStats = plugin.getUnlockManager().getPlayerStatistics(player);
                    MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_INFO_PLAYER_PROGRESS,
                            "%unlocked%", String.valueOf(playerStats.getOrDefault("total_unlocked", 0)),
                            "%total%", String.valueOf(playerStats.getOrDefault("total_available", 0)));
                }

                return true;
            } catch (Exception e) {
                MessageUtils.logError("Error showing plugin info: " + e.getMessage());
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_ERROR_GENERIC);
                return true;
            }
        }
    }
}