package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 银粉 - 使用白糖
public class SilverDust extends Material {
    public SilverDust(Main plugin) {
        super(plugin, "silver_dust", "银粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.SUGAR)
                .displayName("§f银粉")
                .lore("§7贵金属粉末", "§7可用于合成银锭")
                .nbt(nbtKey, "true")
                .build();
    }
}
