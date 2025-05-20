package cn.fandmc.gui.templates;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.StaticItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        for (int slot = 0; slot < size; slot++) {
            if (isEdgeSlot(slot)) {
                addItem(slot, new StaticItem(createEdgeItem()));
            }
        }

        addItem(38, new StaticItem(createPreviousPageItem()));
        addItem(40, new StaticItem(createNextPageItem()));
    }

    private boolean isEdgeSlot(int slot) {
        return (slot < 9) || (slot >= size - 9 && slot < size);
    }

    private ItemStack createEdgeItem() {
        ItemStack edge = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = edge.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            edge.setItemMeta(meta);
        }
        return edge;
    }

    private ItemStack createPreviousPageItem() {
        ItemStack item = new ItemStack(currentPage == 0 ? Material.GRAY_STAINED_GLASS_PANE : Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(currentPage == 0 ? "§7没有上一页" : "§a上一页");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createNextPageItem() {
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

        int guiSlot = 9;
        for (int i = start; i < end; i++) {
            if (guiSlot > 33) break;

            GUIComponent item = allItems.get(i);
            if (item != null) {
                addItem(guiSlot, item);
            }

            guiSlot++;
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
