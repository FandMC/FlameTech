package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.utils.SkullUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ArtificialSapphire extends Material {
    public ArtificialSapphire(Main plugin) {
        super(plugin, "artificial_sapphire", "人造蓝宝石");
    }

    @Override
    public ItemStack createItem() {
        ItemStack skull = SkullUtils.createDefaultSkull(
                "§9人造蓝宝石",
                Arrays.asList("§7人造宝石", "§7工业合成蓝宝石")
        );

        skull = new cn.fandmc.flametech.items.builders.ItemBuilder(skull)
                .nbt(nbtKey, "true")
                .build();

        return processItemDisplay(skull);
    }
}
