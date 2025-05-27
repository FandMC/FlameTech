package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 铜粉 - 使用萤石粉
public class CopperDust extends Material {
    public CopperDust(Main plugin) {
        super(plugin, "copper_dust", "铜粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.GLOWSTONE_DUST)
                .displayName("§6铜粉")
                .lore("§7金属粉末", "§7可用于合成铜锭")
                .nbt(nbtKey, "true")
                .build();
    }
}
