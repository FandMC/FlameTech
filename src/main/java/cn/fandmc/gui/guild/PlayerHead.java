package cn.fandmc.gui.guild;

import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import org.bukkit.Bukkit;
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

    public ItemStack getItem() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (player != null && Bukkit.isOwnedByCurrentRegion(player)) {
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setPlayerProfile(player.getPlayerProfile());
            head.setItemMeta(meta);
        }
        return head;
    }

    @Override
    public void onClick(Player player) {
        GUI.refresh(player);
    }
    @Override
    public String id() {
        return "player_head";
    }
}
