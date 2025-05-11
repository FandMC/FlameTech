package cn.fandmc;

import cn.fandmc.Loader.BukkitLoader;
import cn.fandmc.Loader.FoliaLoader;
import cn.fandmc.config.ConfigManager;
import cn.fandmc.logger.Logger;
import cn.fandmc.recipe.RecipeGUI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private Logger logger;
    private static Main instance;
    private String serverName = Bukkit.getServer().getName();
    private static ConfigManager config;
    public static boolean Folia;
    private RecipeGUI recipeGUI;
    @Override
    public void onEnable() {
        isFolia();
        getLogger().info("\n"+
                "  ______ _                   _______        _     \n" +
                " |  ____| |                 |__   __|      | |    \n" +
                " | |__  | | __ _ _ __ ___   ___| | ___  ___| |__  \n" +
                " |  __| | |/ _` | '_ ` _ \\ / _ \\ |/ _ \\/ __| '_ \\ \n" +
                " | |    | | (_| | | | | | |  __/ |  __/ (__| | | |\n" +
                " |_|    |_|\\__,_|_| |_| |_|\\___|_|\\___|\\___|_| |_|\n"
        );
        getLogger().info(
            String.format("热力科技 - %s [%s]",
            getDescription().getVersion(),
            serverName
        ));
        logger = new Logger(this);
        recipeGUI = new RecipeGUI(this);
        config = new ConfigManager(this);
        instance = this;
        initPlatform();
    }

    @Override
    public void onDisable() {
    }

    private void initPlatform() {
        if (Runtime.version().feature() < 21) {
            throw new UnsupportedOperationException("需要Java21或更高版本");
        }

        if(Folia) {
            getLogger().info("检测到Folia服务端，启用Folia适配模式");
            new FoliaLoader(this, logger, config);
        } else {
            getLogger().info("检测到标准Bukkit服务端，启用传统模式");
            new BukkitLoader(this, logger, config);
        }

    }

    public static Main getPlugin() {
        return instance;
    }

    public static ConfigManager getconfig(){
        return config;
    }

    public void isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            Folia = true;
        } catch (ClassNotFoundException e) {
            Folia = false;
        }
    }
}

