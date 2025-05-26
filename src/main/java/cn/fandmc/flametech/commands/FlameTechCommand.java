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
        MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_HELP_TITLE);
        MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_HELP_LINE1);
        MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_HELP_LINE2);
        MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_HELP_LINE3);
        MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_HELP_LINE4);
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
                MessageUtils.sendMessage(sender, "&c你没有权限执行此命令");
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
                MessageUtils.sendMessage(sender, "&c你没有权限执行此命令");
                return true;
            }

            if (!(sender instanceof Player player)) {
                MessageUtils.sendLocalizedMessage(sender, Messages.COMMAND_GUIDE_ONLY_PLAYER);
                return true;
            }

            try {
                player.getInventory().addItem(BookUtils.createGuideBook());

                String bookName = plugin.getConfigManager().getLang("guide_book.display_name");
                MessageUtils.sendLocalizedMessage(player, Messages.COMMAND_GUIDE_SUCCESS,
                        "%book%", bookName);

                return true;
            } catch (Exception e) {
                MessageUtils.logError("Error giving guide book: " + e.getMessage());
                MessageUtils.sendMessage(player, "&c发放指南书时发生错误");
                return true;
            }
        }
    }

    // 打开GUI命令
    private class OpenCommand implements SubCommand {
        @Override
        public boolean execute(CommandSender sender, String[] args) {
            if (!sender.hasPermission(Permissions.COMMAND_OPEN)) {
                MessageUtils.sendMessage(sender, "&c你没有权限执行此命令");
                return true;
            }

            if (!(sender instanceof Player player)) {
                MessageUtils.sendMessage(sender, "&c只能对玩家执行此操作");
                return true;
            }

            String guiName = args.length > 1 ? args[1] : "main";

            try {
                boolean success = plugin.getGuiManager().openGUI(player, guiName);
                if (!success) {
                    MessageUtils.sendMessage(player, "&c无法打开指定的界面: " + guiName);
                }
                return true;
            } catch (Exception e) {
                MessageUtils.logError("Error opening GUI: " + e.getMessage());
                MessageUtils.sendMessage(player, "&c打开界面时发生错误");
                return true;
            }
        }
    }

    // 重载命令
    private class ReloadCommand implements SubCommand {
        @Override
        public boolean execute(CommandSender sender, String[] args) {
            if (!sender.hasPermission(Permissions.COMMAND_RELOAD)) {
                MessageUtils.sendMessage(sender, "&c你没有权限执行此命令");
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
                if (sender instanceof Player player) {
                    Map<String, Object> playerStats = plugin.getUnlockManager().getPlayerStatistics(player);
                    MessageUtils.sendMessage(sender, "&7解锁进度: &e" +
                            playerStats.getOrDefault("total_unlocked", 0) + "/" +
                            playerStats.getOrDefault("total_available", 0));
                }
                return true;
            }

            try {
                MessageUtils.sendMessage(sender, "&e&l=== FlameTech 插件信息 ===");
                MessageUtils.sendMessage(sender, "&7版本: &e" + plugin.getPluginVersion());
                MessageUtils.sendMessage(sender, "&7已注册物品: &e" + plugin.getItemManager().getRegisteredItemCount());
                MessageUtils.sendMessage(sender, "&7已注册配方: &e" + plugin.getRecipeManager().getRecipeCount());
                MessageUtils.sendMessage(sender, "&7已注册多方块: &e" + plugin.getMultiblockManager().getRegisteredStructureCount());
                MessageUtils.sendMessage(sender, "&7已注册GUI: &e" + plugin.getGuiManager().getRegisteredGUICount());
                MessageUtils.sendMessage(sender, "&7调试模式: &e" + (plugin.isDebugMode() ? "启用" : "禁用"));

                if (sender instanceof Player player) {
                    Map<String, Object> playerStats = plugin.getUnlockManager().getPlayerStatistics(player);
                    MessageUtils.sendMessage(sender, "&7解锁进度: &e" +
                            playerStats.getOrDefault("total_unlocked", 0) + "/" +
                            playerStats.getOrDefault("total_available", 0));
                }

                return true;
            } catch (Exception e) {
                MessageUtils.logError("Error showing plugin info: " + e.getMessage());
                MessageUtils.sendMessage(sender, "&c获取插件信息时发生错误");
                return true;
            }
        }
    }
}