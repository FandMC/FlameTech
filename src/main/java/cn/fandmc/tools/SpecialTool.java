package cn.fandmc.tools;

import cn.fandmc.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
        return "§6§l" + displayName;
    }

    /**
     * 获取工具描述
     */
    protected java.util.List<String> getToolLore() {
        return java.util.Arrays.asList(
                "§7特殊工具",
                "§7具有独特的功能",
                "",
                "§e[FlameTech 工具]"
        );
    }

    public String getToolId() {
        return toolId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
