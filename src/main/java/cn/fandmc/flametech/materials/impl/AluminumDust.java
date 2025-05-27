package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 铝粉 - 使用白糖
public class AluminumDust extends Material {
    public AluminumDust(Main plugin) {
        super(plugin, "aluminum_dust", "铝粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.SUGAR)
                .displayName("§f铝粉")
                .lore("§7金属粉末", "§7可用于合成铝锭")
                .nbt(nbtKey, "true")
                .build();
    }
}
