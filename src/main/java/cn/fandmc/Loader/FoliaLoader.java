package cn.fandmc.Loader;

import cn.fandmc.Main;
import cn.fandmc.command.FlameTechCommands;
import cn.fandmc.command.TabComplete.FlameTechCommand;
import cn.fandmc.config.ConfigManager;
import cn.fandmc.gui.GUI;
import cn.fandmc.item.Book;
import cn.fandmc.logger.Logger;
import cn.fandmc.recipe.RecipeReg;
import cn.fandmc.recipe.list.EnhancedWorkbenchRecipe;
import cn.fandmc.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.util.Objects;

public class FoliaLoader implements Listener {
    private final Main plugin;
    private final Logger logger;
    private final ConfigManager config;
    public FoliaLoader(Main plugin, Logger logger, ConfigManager config) {
        this.plugin = plugin;
        this.logger = logger;
        this.config = config;
        initCommand();
        init();
    }
    private void init(){
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> {
            checkUpdate(null);
        }, 40L);
        new Book(plugin);
        GUI.init(plugin);
        RegRecipe();
    }

    private void RegRecipe() {
        RecipeReg.register(new EnhancedWorkbenchRecipe());
    }

    private void initCommand() {
        Objects.requireNonNull(plugin.getCommand("flametech")).setExecutor(new FlameTechCommands(logger, plugin, config));
        Objects.requireNonNull(plugin.getCommand("flametech")).setTabCompleter(new FlameTechCommand(plugin));
    }

    public void checkUpdate(CommandSender sender) {
        if (!plugin.getConfig().getBoolean("update-checker.enabled")) {
            return;
        }

        Bukkit.getAsyncScheduler().runNow(plugin, task -> {
            try {
                String currentVersion = plugin.getDescription().getVersion();
                UpdateChecker checker = new UpdateChecker(
                        "FandMC/FlameTech",
                        currentVersion,
                        plugin.getLogger()
                );

                String latestVersion = checker.getLatestVersion();
                String normalizedCurrent = currentVersion.replaceAll("[^\\d.]", "");
                String normalizedLatest = latestVersion.replaceAll("[^\\d.]", "");
                String finalMessage;
                if (normalizedCurrent.equals(normalizedLatest)) {
                    finalMessage = "§a当前已是最新版本 (" + currentVersion + ")";
                } else {
                    finalMessage = "§c发现新版本 " + latestVersion + "! 当前版本: " + currentVersion + "\n"
                            + "§b下载地址: https://github.com/FandMC/FlameTech/releases/latest";
                }
                Bukkit.getGlobalRegionScheduler().execute(plugin, () -> {
                    if (sender != null) {
                        Logger.send(sender, finalMessage);
                    } else {
                        Logger.log(finalMessage, plugin);
                    }
                });
            } catch (Exception e) {
                String errorMsg = "更新检查失败: " + e.getMessage();
                plugin.getLogger().warning(errorMsg);
                e.printStackTrace();
                Bukkit.getGlobalRegionScheduler().execute(plugin, () -> {
                    if (sender != null) {
                        sender.sendMessage("§c" + errorMsg);
                    }
                });
            }
        });
    }
}
