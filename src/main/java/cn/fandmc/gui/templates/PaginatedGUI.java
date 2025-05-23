package cn.fandmc.gui.templates;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.StaticItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PaginatedGUI extends GUI {
    protected final List<GUIComponent> pageItems = new ArrayList<>();
    protected int currentPage = 0;

    protected final int[] contentSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    protected final int itemsPerPage = contentSlots.length;

    public PaginatedGUI(Main plugin, String name, String title) {
        super(plugin, name, 54, title);
    }

    @Override
    protected void buildGUI() {
        clearComponents();

        setupBorder();

        setupControlButtons();

        displayCurrentPage();
    }

    @Override
    public void refresh() {
        buildGUI();
        super.refresh();
    }

    protected void setupBorder() {
        ItemStack borderItem = createBorderItem();

        for (int i = 0; i < 9; i++) {
            setComponent(i, new StaticItem(borderItem));
        }

        for (int i = 45; i < 54; i++) {
            if (i != 48 && i != 49 && i != 50) {
                setComponent(i, new StaticItem(borderItem));
            }
        }

        setComponent(9, new StaticItem(borderItem));
        setComponent(17, new StaticItem(borderItem));
        setComponent(18, new StaticItem(borderItem));
        setComponent(26, new StaticItem(borderItem));
        setComponent(27, new StaticItem(borderItem));
        setComponent(35, new StaticItem(borderItem));
        setComponent(36, new StaticItem(borderItem));
        setComponent(44, new StaticItem(borderItem));
    }

    protected void setupControlButtons() {
        setComponent(48, new GUIComponent() {
            @Override
            public ItemStack item() {
                return createPreviousPageButton();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                previousPage(player);
            }
        });

        setComponent(49, new StaticItem(createPageInfoItem()));

        setComponent(50, new GUIComponent() {
            @Override
            public ItemStack item() {
                return createNextPageButton();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                nextPage(player);
            }
        });
    }

    protected void displayCurrentPage() {
        int totalPages = getTotalPages();
        if (totalPages == 0) return;

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, pageItems.size());

        for (int slot : contentSlots) {
            removeComponent(slot);
        }

        for (int i = 0; i < itemsPerPage; i++) {
            int slot = contentSlots[i];
            int itemIndex = startIndex + i;

            if (itemIndex < endIndex) {
                setComponent(slot, pageItems.get(itemIndex));
            }
        }
    }

    public void nextPage(Player player) {
        if (currentPage < getTotalPages() - 1) {
            currentPage++;
            refresh();
        }
    }

    public void previousPage(Player player) {
        if (currentPage > 0) {
            currentPage--;
            refresh();
        }
    }

    public void addPageItem(GUIComponent item) {
        pageItems.add(item);
    }

    public void removePageItem(int index) {
        if (index >= 0 && index < pageItems.size()) {
            pageItems.remove(index);
            if (pageItems.isEmpty()) {
                currentPage = 0;
            } else if (currentPage >= getTotalPages()) {
                currentPage = getTotalPages() - 1;
            }
        }
    }

    public void clearPageItems() {
        pageItems.clear();
        currentPage = 0;
    }

    protected int getTotalPages() {
        return pageItems.isEmpty() ? 1 : (int) Math.ceil((double) pageItems.size() / itemsPerPage);
    }

    protected ItemStack createBorderItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    protected ItemStack createPreviousPageButton() {
        Material material = currentPage > 0 ? Material.ARROW : Material.GRAY_STAINED_GLASS_PANE;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(currentPage > 0 ? "§a← 上一页" : "§7没有上一页");
            item.setItemMeta(meta);
        }
        return item;
    }

    protected ItemStack createNextPageButton() {
        Material material = currentPage < getTotalPages() - 1 ? Material.ARROW : Material.GRAY_STAINED_GLASS_PANE;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(currentPage < getTotalPages() - 1 ? "§a下一页 →" : "§7没有下一页");
            item.setItemMeta(meta);
        }
        return item;
    }

    protected ItemStack createPageInfoItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e页面信息");
            meta.setLore(Arrays.asList(
                    "§7当前页: §e" + (currentPage + 1) + "§7/§e" + getTotalPages(),
                    "§7物品总数: §e" + pageItems.size()
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
}