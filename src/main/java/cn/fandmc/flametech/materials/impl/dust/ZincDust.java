package cn.fandmc.flametech.materials.impl.dust;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 锌粉 - 使用白糖
public class ZincDust extends Material {
    public ZincDust(Main plugin) {
        super(plugin, "zinc_dust", "锌粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.SUGAR)
                .displayName("§f锌粉")
                .lore("§7金属粉末", "§7可用于合成锌锭")
                .nbt(nbtKey, "true")
                .build();
    }
}
