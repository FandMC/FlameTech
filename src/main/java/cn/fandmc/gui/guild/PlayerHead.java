package cn.fandmc.gui.guild;

import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHead implements GUIComponent {
    private final int slot;
    private final Player player;

    public PlayerHead(int slot, Player player) {
        this.slot = slot;
        this.player = player;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public ItemStack getItem() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setPlayerProfile(player.getPlayerProfile());
        meta.setDisplayName("§a" + player.getName());

        head.setItemMeta(meta);
        return head;
    }

    @Override
    public void onClick(Player player) {
        GUI.registerComponent(new PlayerHead(slot, player));
        GUI.open(player);
    }

    @Override
    public int id() {
        return 2;
    }
}
