package cn.fandmc.flametech.materials.impl.dust;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import org.bukkit.inventory.ItemStack;

public class MagnesiumDust extends Material {
    public MagnesiumDust(Main plugin) {
        super(plugin, "magnesium_dust", "镁粉");
    }

    @Override
    public ItemStack createItem() {
        return new ItemBuilder(org.bukkit.Material.SUGAR)
                .displayName("§f镁粉")
                .lore("§7轻金属粉末", "§7易燃，小心处理", "§c⚠ 遇水剧烈反应")
                .nbt(nbtKey, "true")
                .build();
    }
}