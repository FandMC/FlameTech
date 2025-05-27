package cn.fandmc.flametech.materials.base;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.ArrayList;

public abstract class Material {
    protected final Main plugin;
    protected final String materialId;
    protected final String displayName;
    protected final String nbtKey;

    public Material(Main plugin, String materialId, String displayName) {
        this.plugin = plugin;
        this.materialId = materialId;
        this.displayName = displayName;
        this.nbtKey = "flametech_material_" + materialId;
    }

    public abstract ItemStack createItem();

    public ItemStack createItem(int amount) {
        ItemStack item = createItem();
        item.setAmount(Math.max(1, Math.min(64, amount)));
        return item;
    }

    /**
     * 安全地处理物品描述，确保颜色代码被正确处理
     */
    protected ItemStack processItemDisplay(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();

        // 处理显示名称
        if (meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            meta.setDisplayName(MessageUtils.colorize(displayName));
        }

        // 处理lore
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                List<String> processedLore = new ArrayList<>();
                for (String line : lore) {
                    processedLore.add(MessageUtils.colorize(line));
                }
                meta.setLore(processedLore);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    public boolean isMaterial(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .has(new org.bukkit.NamespacedKey(plugin, nbtKey),
                        org.bukkit.persistence.PersistentDataType.STRING);
    }

    public String getMaterialId() { return materialId; }
    public String getDisplayName() { return displayName; }
}