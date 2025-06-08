package cn.fandmc.flametech.materials.impl.dust;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 锡粉 - 使用白糖
public class TinDust extends Material {
    public TinDust(Main plugin) {
        super(plugin, "tin_dust", "锡粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.SUGAR)
                .displayName("§7锡粉")
                .lore("§7金属粉末", "§7可用于合成锡锭")
                .nbt(nbtKey, "true")
                .build();
    }
}
