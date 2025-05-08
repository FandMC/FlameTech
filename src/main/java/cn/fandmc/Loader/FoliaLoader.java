package cn.fandmc.Loader;

import cn.fandmc.Main;
import cn.fandmc.command.FlameTechCommands;
import cn.fandmc.command.TabComplete.FlameTechCommand;
import cn.fandmc.config.ConfigManager;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.item.StrangeTool.listener.ExplosiveListener;
import cn.fandmc.gui.item.StrangeTool.listener.folia.SmeltingListener;
import cn.fandmc.item.Book;
import cn.fandmc.recipe.CraftingListener;
import cn.fandmc.recipe.impl.*;
import cn.fandmc.structure.StructureListener;
import cn.fandmc.logger.Logger;
import cn.fandmc.recipe.RecipeRegistry;
import cn.fandmc.recipe.impl.EnhancedWorkbenchRecipe;
import cn.fandmc.structure.StructureManager;
import cn.fandmc.structure.impl.*;
import cn.fandmc.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

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
        StructureManager.registerStructure(new EnhancedWorkbenchStructure());
        plugin.getServer().getPluginManager().registerEvents(new StructureListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CraftingListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SmeltingListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ExplosiveListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EnhancedCraftingListener(), plugin);
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> checkUpdate(null), 40L);
        new Book(plugin);
        GUI.init(plugin);
        RegRecipe();
    }

    private void RegRecipe() {
        RecipeRegistry.register(new EnhancedWorkbenchRecipe());
        RecipeRegistry.register(new SmeltingPickaxeRecipe());
        RecipeRegistry.register(new ExplosivePickaxeRecipe());
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
                String finalMessage = getString();
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

    private @NotNull String getString() {
        String currentVersion = plugin.getDescription().getVersion();
        UpdateChecker checker = new UpdateChecker(
                "FandMC/FlameTech",
                currentVersion,
                plugin.getLogger()
        );

        String latestVersion = checker.getLatestVersion();
        if (latestVersion == null) {
            return "§c无法获取最新版本信息";
        }

        String normalizedCurrent = currentVersion.replaceAll("[^\\d.]", "");
        String normalizedLatest = latestVersion.replaceAll("[^\\d.]", "");

        int versionComparison = compareVersions(normalizedCurrent, normalizedLatest);
        String finalMessage;

        if (versionComparison == 0) {
            finalMessage = "§a当前已是最新版本 (" + currentVersion + ")";
        } else if (versionComparison < 0) {
            finalMessage = "§c发现新版本 " + latestVersion + "! 当前版本: " + currentVersion + "\n"
                    + "§b下载地址: https://github.com/FandMC/FlameTech/releases/latest";
        } else {
            finalMessage = "§e当前为测试版本 (" + currentVersion + ")，最新正式版为 " + latestVersion;
        }
        return finalMessage;
    }

    private int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int maxLength = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLength; i++) {
            int num1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
            int num2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;
            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        return 0;
    }

}
