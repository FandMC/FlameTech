package cn.fandmc.flametech.materials.impl.ingot;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class MagnesiumIngot extends Material {
    public MagnesiumIngot(Main plugin) {
        super(plugin, "magnesium_ingot", "镁锭");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.IRON_INGOT)
                .displayName("§f镁锭")
                .lore("§7轻金属锭", "§7高活性金属", "§c⚠ 遇水剧烈反应")
                .nbt(nbtKey, "true")
                .build();
    }
}