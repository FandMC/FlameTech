package cn.fandmc.flametech.materials.impl.ingot;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class CobaltIngot extends Material {
    public CobaltIngot(Main plugin) {
        super(plugin, "cobalt_ingot", "钴锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.GOLD_INGOT)
                .displayName("§9钴锭")
                .lore("§7稀有金属锭", "§7用于制造合金")
                .nbt(nbtKey, "true")
                .build();
    }
}