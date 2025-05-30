package cn.fandmc.flametech.materials.impl.ingot;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 钛锭 - 使用铁锭
public class TitaniumIngot extends Material {
    public TitaniumIngot(Main plugin) {
        super(plugin, "titanium_ingot", "钛锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.IRON_INGOT)
                .displayName("§f钛锭")
                .lore("§7高强度金属锭", "§7轻质高强度材料")
                .nbt(nbtKey, "true")
                .glow()
                .build();
    }
}