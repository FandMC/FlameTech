package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.utils.SkullUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RawSilicon extends Material {
    public RawSilicon(Main plugin) {
        super(plugin, "raw_silicon", "原硅");
    }

    @Override
    public ItemStack createItem() {
        ItemStack skull = SkullUtils.createDefaultSkull(
                "§8原硅",
                Arrays.asList("§7原始硅材料", "§7需要精炼处理")
        );

        skull = new cn.fandmc.flametech.items.builders.ItemBuilder(skull)
                .nbt(nbtKey, "true")
                .build();

        return processItemDisplay(skull);
    }
}
