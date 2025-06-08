package cn.fandmc.flametech.materials.impl.dust;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class CarbonDust extends Material {
    public CarbonDust(Main plugin) {
        super(plugin, "carbon_dust", "碳粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.GUNPOWDER)
                .displayName("§8碳粉")
                .lore("§7碳的粉末", "§7高纯度碳材料")
                .nbt(nbtKey, "true")
                .build();
    }
}