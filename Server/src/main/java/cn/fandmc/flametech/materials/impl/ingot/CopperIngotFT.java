package cn.fandmc.flametech.materials.impl.ingot;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class CopperIngotFT extends Material {
    public CopperIngotFT(Main plugin) {
        super(plugin, "copper_ingot_ft", "铜锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.COPPER_INGOT)
                .displayName("§6铜锭")
                .lore("§7金属锭", "§7工业纯铜")
                .nbt(nbtKey, "true")
                .glow()
                .build();
    }
}