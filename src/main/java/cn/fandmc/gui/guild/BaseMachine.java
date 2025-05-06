package cn.fandmc.gui.guild;

import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.gui.BaseMachineGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class BaseMachine implements GUIComponent {
    @Override
    public int getSlot() { return 10; }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a基础的机器");
        meta.setLore(List.of("§7> 点击打开"));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onClick(Player player) {
        BaseMachineGUI.open(player);
    }

    @Override
    public int id() { return 1; }
}
