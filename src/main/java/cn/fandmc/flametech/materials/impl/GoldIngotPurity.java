package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 金锭纯度基类
public abstract class GoldIngotPurity extends Material {
    protected final int purity;

    public GoldIngotPurity(Main plugin, String materialId, String displayName, int purity) {
        super(plugin, materialId, displayName);
        this.purity = purity;
    }

    @Override
    public ItemStack createItem() {
        String purityColor = getPurityColor(purity);
        return new ItemBuilder(org.bukkit.Material.GOLD_INGOT)
                .displayName(purityColor + getDisplayName())
                .lore(
                        "§7纯度锭",
                        "§7纯度: " + purityColor + purity + "%",
                        ""
                )
                .nbt(nbtKey, "true")
                .nbt("flametech_purity", purity)
                .build();
    }

    private String getPurityColor(int purity) {
        if (purity >= 90) return "§a";
        if (purity >= 70) return "§e";
        if (purity >= 50) return "§6";
        if (purity >= 30) return "§c";
        return "§4";
    }
}
