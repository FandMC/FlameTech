package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class TinIngot extends Material {
    public TinIngot(Main plugin) {
        super(plugin, "tin_ingot", "锡锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.IRON_INGOT)
                .displayName("§7锡锭")
                .lore("§7金属锭", "§7低熔点金属")
                .nbt(nbtKey, "true")
                .build();
    }
}