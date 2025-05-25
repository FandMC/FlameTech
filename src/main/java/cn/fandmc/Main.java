package cn.fandmc;

import cn.fandmc.config.Config;
import cn.fandmc.config.ConfigManager;
import cn.fandmc.commands.*;
import cn.fandmc.gui.BookClickListener;
import cn.fandmc.gui.GUIListener;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.impl.MainGUI;
import cn.fandmc.gui.impl.BasicMachinesGUI;
import cn.fandmc.gui.impl.ToolsGUI;
import cn.fandmc.machines.basic.EnhancedCraftingGUI;
import cn.fandmc.machines.basic.EnhancedCraftingTable;
import cn.fandmc.multiblock.MultiblockManager;
import cn.fandmc.recipe.RecipeManager;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.ShapedRecipe;
import cn.fandmc.recipes.tool.*;
import cn.fandmc.tools.ToolManager;
import cn.fandmc.tools.ToolListener;
import cn.fandmc.unlock.UnlockManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main extends JavaPlugin {

    private String serverName = Bukkit.getServer().getName();
    private static Main instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        getLogger().info("""
                
                  ______ _                   _______        _    \s
                 |  ____| |                 |__   __|      | |   \s
                 | |__  | | __ _ _ __ ___   ___| | ___  ___| |__ \s
                 |  __| | |/ _` | '_ ` _ \\ / _ \\ |/ _ \\/ __| '_ \\\s
                 | |    | | (_| | | | | | |  __/ |  __/ (__| | | |
                 |_|    |_|\\__,_|_| |_| |_|\\___|_|\\___|\\___|_| |_|
                """
        );
        getLogger().info(
                String.format("热力科技 - %s [%s]",
                        Config.getversion,
                        serverName
                ));

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        instance = this;

        Objects.requireNonNull(getCommand("FlameTech")).setExecutor(new FlameTechCommand(this));
        Objects.requireNonNull(getCommand("FlameTech")).setTabCompleter(new FlameTechTabCompleter());

        try {
            this.configManager = new ConfigManager(this);

            saveDefaultConfig();

            ToolManager.init(this);

            UnlockManager.init(this);

            GUIManager.init(this);
            MainGUI.getInstance(this);
            BasicMachinesGUI.getInstance(this);
            ToolsGUI.getInstance(this);

            MultiblockManager.init(this);
            registerMultiblocks();

            RecipeManager.init(this);
            registerRecipes();

            registerMachineGUIs();
            registerListeners();
            registerUnlockables();

        } catch (Exception e) {
            getLogger().severe("插件初始化失败: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        getLogger().info("FlameTech 启动完成!");
    }

    @Override
    public void onDisable() {
        if (UnlockManager.getInstance() != null) {
            UnlockManager.getInstance().saveAllData();
        }
        getLogger().info("FlameTech 已关闭!");
    }

    private void registerMultiblocks() {
        MultiblockManager.getInstance().registerStructure(new EnhancedCraftingTable());
    }

    private void registerRecipes() {
        RecipeManager manager = RecipeManager.getInstance();

        manager.registerRecipe(ExplosivePickaxeRecipe.create());
        manager.registerRecipe(SmeltingPickaxeRecipe.create());
    }

    private void registerMachineGUIs() {
        new EnhancedCraftingGUI(this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ToolListener(this), this);
    }

    private void registerUnlockables() {
        UnlockManager manager = UnlockManager.getInstance();

        manager.registerUnlockable(new UnlockManager.UnlockableItem("multiblock.enhanced_crafting_table", 10));
        manager.registerUnlockable(new UnlockManager.UnlockableItem("recipe.explosive_pickaxe", 15));
        manager.registerUnlockable(new UnlockManager.UnlockableItem("recipe.smelting_pickaxe", 20));
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void reloadLang() {
        configManager.reloadConfig();
    }
}