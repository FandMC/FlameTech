package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.utils.SkullUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Silicon extends Material {
    public Silicon(Main plugin) {
        super(plugin, "silicon", "硅");
    }

    @Override
    public ItemStack createItem() {
        ItemStack skull = SkullUtils.createDefaultSkull(
                "§7硅",
                Arrays.asList("§7半导体材料", "§7高纯度硅")
        );

        skull = new cn.fandmc.flametech.items.builders.ItemBuilder(skull)
                .nbt(nbtKey, "true")
                .build();

        return processItemDisplay(skull);
    }
}
