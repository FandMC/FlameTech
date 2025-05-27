package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 煤粉 - 使用火药
public class CoalDust extends Material {
    public CoalDust(Main plugin) {
        super(plugin, "coal_dust", "煤粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.GUNPOWDER)
                .displayName("§8煤粉")
                .lore("§7燃料粉末", "§7高效燃烧材料")
                .nbt(nbtKey, "true")
                .build();
    }
}
