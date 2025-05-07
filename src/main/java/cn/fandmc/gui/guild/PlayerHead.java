package cn.fandmc.gui.guild;

import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHead implements GUIComponent {
    private final int slot;
    private Player player;

    public PlayerHead(int slot) {
        this.slot = slot;
    }

    public PlayerHead withPlayer(Player player) {
        this.player = player;
        return this;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public ItemStack getItem() {
        if (player == null) return new ItemStack(Material.PLAYER_HEAD);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setPlayerProfile(player.getPlayerProfile());
        meta.setDisplayName("§a" + player.getName());
        head.setItemMeta(meta);
        return head;
    }

    @Override
    public void onClick(Player player) {
        GUI.refresh(player);
    }
    @Override
    public int id() {
        return 3;
    }
}
