package cn.fandmc.flametech.tools;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public abstract class SpecialTool {
    protected final Main plugin;
    protected final String toolId;
    protected final String displayName;
    protected final NamespacedKey toolKey;

    public SpecialTool(Main plugin, String toolId, String displayName) {
        this.plugin = plugin;
        this.toolId = toolId;
        this.displayName = displayName;
        this.toolKey = new NamespacedKey(plugin, toolId);
    }

    /**
     * 检查物品是否为此特殊工具
     */
    public boolean isTool(ItemStack item) {
        if (item == null || item.getType() != Material.IRON_PICKAXE) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(toolKey, PersistentDataType.BYTE);
    }

    /**
     * 创建工具物品
     */
    public ItemStack createTool() {
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(getFormattedDisplayName());
            meta.setLore(getToolLore());

            // 设置NBT标记
            meta.getPersistentDataContainer().set(toolKey, PersistentDataType.BYTE, (byte) 1);
            pickaxe.setItemMeta(meta);
        }

        return pickaxe;
    }

    /**
     * 处理方块破坏事件
     */
    public abstract void handleBlockBreak(BlockBreakEvent event, Player player, Block block, ItemStack tool);

    /**
     * 检查工具是否启用
     */
    public abstract boolean isEnabled();

    /**
     * 获取格式化的显示名称
     */
    protected String getFormattedDisplayName() {
        try {
            // 从配置文件获取显示名称格式
            String format = plugin.getConfigManager().getLang(Messages.TOOLS_SPECIAL_TOOL_DISPLAY_NAME_FORMAT);

            // 替换 %name% 参数
            String formattedName = format.replace("%name%", displayName);

            // 应用颜色代码
            return MessageUtils.colorize(formattedName);

        } catch (Exception e) {
            // 如果配置加载失败，使用默认格式
            MessageUtils.logWarning("Failed to load special tool display name format, using default: " + e.getMessage());
            return MessageUtils.colorize("&6&l" + displayName);
        }
    }

    /**
     * 获取工具描述
     */
    protected List<String> getToolLore() {
       List<String> lore = plugin.getConfigManager().getStringList(Messages.TOOLS_SPECIAL_TOOL_DEFAULT_LORE);
       return MessageUtils.colorize(lore);

    }

    /**
     * 获取本地化的工具描述（供子类重写）
     */
    protected List<String> getLocalizedToolLore() {
        return getToolLore();
    }

    /**
     * 获取工具信息
     */
    public java.util.Map<String, Object> getToolInfo() {
        java.util.Map<String, Object> info = new java.util.HashMap<>();
        info.put("id", toolId);
        info.put("display_name", displayName);
        info.put("formatted_display_name", getFormattedDisplayName());
        info.put("enabled", isEnabled());
        info.put("lore_lines", getToolLore().size());
        return info;
    }

    public String getToolId() {
        return toolId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Main getPlugin() {
        return plugin;
    }

    public NamespacedKey getToolKey() {
        return toolKey;
    }
}