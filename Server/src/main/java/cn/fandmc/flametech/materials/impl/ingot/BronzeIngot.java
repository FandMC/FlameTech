package cn.fandmc.flametech.materials.impl.ingot;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 青铜锭
public class BronzeIngot extends Material {
    public BronzeIngot(Main plugin) {
        super(plugin, "bronze_ingot", "青铜锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.COPPER_INGOT)
                .displayName("§6青铜锭")
                .lore("§7合金锭", "§7由铜和锡合成")
                .nbt(nbtKey, "true")
                .build();
    }
}
