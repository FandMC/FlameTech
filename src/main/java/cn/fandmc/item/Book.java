package cn.fandmc.item;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.util.LangUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class Book implements Listener {
    private static final NamespacedKey BOOK_KEY = new NamespacedKey(Main.getPlugin(), "guide_book");
    private final Main plugin;

    public Book(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void giveGuideBook(Player player) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();

        meta.setDisplayName( "§aFlameTech" + LangUtil.get("Item.FlameTechManual.DisplayName"));
        meta.setLore(Arrays.asList(
                "§7»" + LangUtil.get("Item.FlameTechManual.Lore")
        ));

        meta.getPersistentDataContainer().set(BOOK_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        book.setItemMeta(meta);
        player.getInventory().addItem(book);
    }

    @EventHandler
    public void onBookUse(PlayerInteractEvent event) {
        if (event.getAction().name().contains("RIGHT_CLICK")) {
            ItemStack item = event.getItem();
            if (isGuideBook(item)) {
                event.setCancelled(true);
                GUI.open(event.getPlayer(), "main");
            }
        }
    }

    private boolean isGuideBook(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(BOOK_KEY, PersistentDataType.BYTE);
    }
}
