package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 金粉 - 使用萤石粉
public class GoldDust extends Material {
    public GoldDust(Main plugin) {
        super(plugin, "gold_dust", "金粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.GLOWSTONE_DUST)
                .displayName("§e金粉")
                .lore("§7贵金属粉末", "§7可用于合成金锭")
                .nbt(nbtKey, "true")
                .build();
    }
}
