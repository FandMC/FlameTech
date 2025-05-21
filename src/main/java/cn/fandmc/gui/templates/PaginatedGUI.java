package cn.fandmc.gui.templates;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.StaticItem;
import cn.fandmc.gui.GUIComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedGUI extends GUI {
    private final int ITEMS_PER_PAGE = 21;
    private int currentPage = 0;
    private final List<GUIComponent> allItems = new ArrayList<>();

    public PaginatedGUI(Main plugin, String name, String title, int size) {
        super(plugin, name, size, title);
        setupDefaultItems();
    }

    protected void setupDefaultItems() {
        for (int i = 0; i < size; i++) {
            if (isEdgeSlot(i)) {
                addItem(i, new StaticItem(createEdgeItem()));
            }
        }

        if (size >= 13) {
            Player player = Main.getInstance().getServer().getOnlinePlayers().iterator().hasNext() ?
                    Main.getInstance().getServer().getOnlinePlayers().iterator().next() : null;

            if (player != null) {
                addItem(4, new StaticItem(createPlayerHead(player.getName())));
            }
        }

        addItem(38, new StaticItem(createPreviousPageItem()));
        addItem(40, new StaticItem(createNextPageItem()));
    }

    protected boolean isEdgeSlot(int slot) {
        return (slot < 9) || (slot >= size - 9);
    }

    protected ItemStack createEdgeItem() {
        ItemStack edge = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = edge.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            edge.setItemMeta(meta);
        }
        return edge;
    }

    protected ItemStack createPlayerHead(String playerName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getPlayer(playerName));
            meta.setDisplayName("§a" + playerName + " 的头颅");
            head.setItemMeta(meta);
        }
        return head;
    }

    protected ItemStack createPreviousPageItem() {
        int totalPages = (int) Math.ceil((double) allItems.size() / ITEMS_PER_PAGE);
        ItemStack item = new ItemStack(currentPage == 0 ? Material.GRAY_STAINED_GLASS_PANE : Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(currentPage == 0 ? "§7没有上一页" : "§a上一页");
            item.setItemMeta(meta);
        }
        return item;
    }

    protected ItemStack createNextPageItem() {
        int totalPages = (int) Math.ceil((double) allItems.size() / ITEMS_PER_PAGE);
        ItemStack item = new ItemStack(currentPage >= totalPages - 1 ? Material.GRAY_STAINED_GLASS_PANE : Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(currentPage >= totalPages - 1 ? "§7没有下一页" : "§a下一页");
            item.setItemMeta(meta);
        }
        return item;
    }

    public void refreshPage(Player player) {
        this.guiItems.clear();
        setupDefaultItems();

        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allItems.size());
        int[] fillSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                27, 28, 29, 30, 31, 32, 33
        };

        for (int i = 0; i < fillSlots.length && start + i < end; i++) {
            int slot = fillSlots[i];
            GUIComponent item = allItems.get(start + i);
            if (slot < size) {
                addItem(slot, item);
            }
        }

        buildInventory();

        if (player != null) {
            open(player);
        }
    }

    public void previousPage(Player player) {
        if (currentPage > 0) {
            currentPage--;
            refreshPage(player);
        }
    }

    public void nextPage(Player player) {
        int totalPages = (int) Math.ceil((double) allItems.size() / ITEMS_PER_PAGE);
        if (currentPage < totalPages - 1) {
            currentPage++;
            refreshPage(player);
        }
    }

    public void addPageItem(GUIComponent component) {
        allItems.add(component);
    }

    public void addPageItem(int index, GUIComponent component) {
        allItems.add(index, component);
    }
}
