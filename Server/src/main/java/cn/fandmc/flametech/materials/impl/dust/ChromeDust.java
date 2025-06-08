package cn.fandmc.flametech.materials.impl.dust;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

// 铬粉 - 使用白糖
public class ChromeDust extends Material {
    public ChromeDust(Main plugin) {
        super(plugin, "chrome_dust", "铬粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.SUGAR)
                .displayName("§8铬粉")
                .lore("§7金属粉末", "§7可用于合成铬锭")
                .nbt(nbtKey, "true")
                .build();
    }
}
