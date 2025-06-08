package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.utils.SkullUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

// 人造绿宝石 - 使用头颅
public class ArtificialEmerald extends Material {
    public ArtificialEmerald(Main plugin) {
        super(plugin, "artificial_emerald", "人造绿宝石");
    }

    @Override
    public ItemStack createItem() {
        ItemStack skull = SkullUtils.createDefaultSkull(
                "§a人造绿宝石",
                Arrays.asList("§7人造宝石", "§7工业合成绿宝石")
        );

        skull = new cn.fandmc.flametech.items.builders.ItemBuilder(skull)
                .nbt(nbtKey, "true")
                .build();

        return processItemDisplay(skull);
    }
}
