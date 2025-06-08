package cn.fandmc.flametech.materials.impl.dust;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 矿粉 - 一切的起源
public class OreDust extends Material {
    public OreDust(Main plugin) {
        super(plugin, "ore_dust", "矿粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.GRAVEL)
                .displayName("§8矿粉")
                .lore("§7原始矿粉", "§7一切的起源", "§7可加工成各种金属粉末")
                .nbt(nbtKey, "true")
                .build();
    }
}
