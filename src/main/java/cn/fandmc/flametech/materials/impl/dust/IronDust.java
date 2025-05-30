package cn.fandmc.flametech.materials.impl.dust;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import org.bukkit.inventory.ItemStack;

// 铁粉 - 使用火药
public class IronDust extends Material {
    public IronDust(Main plugin) {
        super(plugin, "iron_dust", "铁粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.GUNPOWDER)
                .displayName("§7铁粉")
                .lore("§7金属粉末", "§7可用于合成铁锭")
                .nbt(nbtKey, "true")
                .build();
    }
}
