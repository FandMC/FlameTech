package cn.fandmc.flametech.materials.impl.ingot;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class ZincIngot extends Material {
    public ZincIngot(Main plugin) {
        super(plugin, "zinc_ingot", "锌锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.IRON_INGOT)
                .displayName("§7锌锭")
                .lore("§7金属锭", "§7用于合金制造")
                .nbt(nbtKey, "true")
                .build();
    }
}