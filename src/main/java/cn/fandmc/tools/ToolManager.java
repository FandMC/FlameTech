package cn.fandmc.tools;

import cn.fandmc.Main;
import cn.fandmc.tools.impl.ExplosivePickaxe;
import cn.fandmc.tools.impl.SmeltingPickaxe;
import org.bukkit.inventory.ItemStack;

public class ToolManager {
    private static ToolManager instance;
    private final Main plugin;
    private final ExplosivePickaxe explosivePickaxe;
    private final SmeltingPickaxe smeltingPickaxe;

    private ToolManager(Main plugin) {
        this.plugin = plugin;
        this.explosivePickaxe = new ExplosivePickaxe(plugin);
        this.smeltingPickaxe = new SmeltingPickaxe(plugin);
    }

    public static void init(Main plugin) {
        if (instance == null) {
            instance = new ToolManager(plugin);
        }
    }

    public static ToolManager getInstance() {
        return instance;
    }

    /**
     * 检查是否为爆炸镐
     */
    public boolean isExplosivePickaxe(ItemStack item) {
        return explosivePickaxe.isTool(item);
    }

    /**
     * 检查是否为熔炼镐
     */
    public boolean isSmeltingPickaxe(ItemStack item) {
        return smeltingPickaxe.isTool(item);
    }

    /**
     * 创建爆炸镐
     */
    public ItemStack createExplosivePickaxe() {
        return explosivePickaxe.createTool();
    }

    /**
     * 创建熔炼镐
     */
    public ItemStack createSmeltingPickaxe() {
        return smeltingPickaxe.createTool();
    }

    /**
     * 检查是否启用爆炸镐
     */
    public boolean isExplosivePickaxeEnabled() {
        return explosivePickaxe.isEnabled();
    }

    /**
     * 检查是否启用熔炼镐
     */
    public boolean isSmeltingPickaxeEnabled() {
        return smeltingPickaxe.isEnabled();
    }

    /**
     * 获取爆炸镐实例
     */
    public ExplosivePickaxe getExplosivePickaxe() {
        return explosivePickaxe;
    }

    /**
     * 获取熔炼镐实例
     */
    public SmeltingPickaxe getSmeltingPickaxe() {
        return smeltingPickaxe;
    }
}