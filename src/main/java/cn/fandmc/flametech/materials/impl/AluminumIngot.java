package cn.fandmc.flametech.materials.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class AluminumIngot extends Material {
    public AluminumIngot(Main plugin) {
        super(plugin, "aluminum_ingot", "铝锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.IRON_INGOT)
                .displayName("§f铝锭")
                .lore("§7轻金属锭", "§7轻质高强度")
                .nbt(nbtKey, "true")
                .build();
    }
}