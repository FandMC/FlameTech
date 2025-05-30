package cn.fandmc.flametech.materials.impl.ingot;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class SilverIngot extends Material {
    public SilverIngot(Main plugin) {
        super(plugin, "silver_ingot", "银锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.IRON_INGOT)
                .displayName("§f银锭")
                .lore("§7贵金属锭", "§7导电性极佳")
                .nbt(nbtKey, "true")
                .glow()
                .build();
    }
}