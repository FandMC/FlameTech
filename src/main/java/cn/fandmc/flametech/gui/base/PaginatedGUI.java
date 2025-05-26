package cn.fandmc.flametech.gui.base;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.gui.components.NavigationComponent;
import cn.fandmc.flametech.gui.components.StaticComponent;
import cn.fandmc.flametech.gui.components.UnlockableComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页GUI基类
 */
public abstract class PaginatedGUI extends BaseGUI {

    protected final List<GUIComponent> pageItems = new ArrayList<>();
    protected int currentPage = 0;
    protected final int[] contentSlots;
    protected final int itemsPerPage;

    // 控制按钮槽位
    protected final int previousPageSlot;
    protected final int pageInfoSlot;
    protected final int nextPageSlot;

    public PaginatedGUI(Main plugin, String guiId, String title) {
        this(plugin, guiId, 54, title, getDefaultContentSlots(), 48, 49, 50);
    }

    public PaginatedGUI(Main plugin, String guiId, int size, String title, int[] contentSlots,
                        int previousSlot, int infoSlot, int nextSlot) {
        super(plugin, guiId, size, title);
        this.contentSlots = contentSlots;
        this.itemsPerPage = contentSlots.length;
        this.previousPageSlot = previousSlot;
        this.pageInfoSlot = infoSlot;
        this.nextPageSlot = nextSlot;
    }

    private static int[] getDefaultContentSlots() {
        return new int[]{
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };
    }

    @Override
    protected void buildGUI(Player player) {
        // 设置边框
        setupBorder();

        // 设置控制按钮
        setupControlButtons(player);

        // 显示当前页内容
        displayCurrentPage(player);

        // 子类可以添加额外的组件
        buildExtraComponents(player);
    }

    protected void setupBorder() {
        var borderItem = ItemBuilder.createBorderItem();
        var borderComponent = new StaticComponent(borderItem);

        // 顶部和底部边框
        for (int i = 0; i < 9; i++) {
            setComponent(i, borderComponent);
            if (size > 45) {
                setComponent(45 + i, borderComponent);
            }
        }

        // 侧边边框
        for (int row = 1; row < (size / 9) - 1; row++) {
            setComponent(row * 9, borderComponent);
            setComponent(row * 9 + 8, borderComponent);
        }
    }

    protected void setupControlButtons(Player player) {
        // 上一页按钮
        setComponent(previousPageSlot, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                return currentPage > 0 ?
                        ItemBuilder.createPreviousPageButton() :
                        new ItemBuilder(org.bukkit.Material.GRAY_STAINED_GLASS_PANE)
                                .displayName("&7没有上一页")
                                .build();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                if (currentPage > 0) {
                    previousPage(player);
                }
            }

            @Override
            public boolean isClickable(Player player) {
                return currentPage > 0;
            }
        });

        // 页面信息
        setComponent(pageInfoSlot, new StaticComponent(
                ItemBuilder.createPageInfoItem(currentPage + 1, getTotalPages(), pageItems.size())
        ));

        // 下一页按钮
        setComponent(nextPageSlot, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                return currentPage < getTotalPages() - 1 ?
                        ItemBuilder.createNextPageButton() :
                        new ItemBuilder(org.bukkit.Material.GRAY_STAINED_GLASS_PANE)
                                .displayName("&7没有下一页")
                                .build();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                if (currentPage < getTotalPages() - 1) {
                    nextPage(player);
                }
            }

            @Override
            public boolean isClickable(Player player) {
                return currentPage < getTotalPages() - 1;
            }
        });
    }

    protected void displayCurrentPage(Player player) {
        // 清空内容槽位
        for (int slot : contentSlots) {
            removeComponent(slot);
        }

        if (pageItems.isEmpty()) {
            return;
        }

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, pageItems.size());

        for (int i = 0; i < itemsPerPage && (startIndex + i) < endIndex; i++) {
            int slot = contentSlots[i];
            GUIComponent item = pageItems.get(startIndex + i);

            // 对于UnlockableComponent，使用玩家特定的显示
            if (item instanceof UnlockableComponent unlockableItem) {
                setComponent(slot, new PlayerSpecificUnlockableWrapper(unlockableItem, player));
            } else {
                setComponent(slot, item);
            }
        }
    }

    /**
     * 包装器，用于为特定玩家显示UnlockableComponent的正确状态
     */
    private static class PlayerSpecificUnlockableWrapper implements GUIComponent {
        private final UnlockableComponent original;
        private final Player player;

        public PlayerSpecificUnlockableWrapper(UnlockableComponent original, Player player) {
            this.original = original;
            this.player = player;
        }

        @Override
        public ItemStack getDisplayItem() {
            return original.getDisplayItemForPlayer(player);
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            original.onClick(player, event);
        }

        @Override
        public boolean isVisible(Player player) {
            return original.isVisible(player);
        }

        @Override
        public boolean isClickable(Player player) {
            return original.isClickable(player);
        }
    }

    /**
     * 子类可以重写此方法添加额外组件
     */
    protected void buildExtraComponents(Player player) {
        // 默认空实现
    }

    /**
     * 下一页
     */
    public void nextPage(Player player) {
        if (currentPage < getTotalPages() - 1) {
            currentPage++;
            refresh();
        }
    }

    /**
     * 上一页
     */
    public void previousPage(Player player) {
        if (currentPage > 0) {
            currentPage--;
            refresh();
        }
    }

    /**
     * 跳转到指定页
     */
    public void goToPage(int page, Player player) {
        if (page >= 0 && page < getTotalPages()) {
            currentPage = page;
            refresh();
        }
    }

    /**
     * 添加页面项目
     */
    public void addPageItem(GUIComponent item) {
        if (item != null) {
            pageItems.add(item);
        }
    }

    /**
     * 移除页面项目
     */
    public void removePageItem(int index) {
        if (index >= 0 && index < pageItems.size()) {
            pageItems.remove(index);

            // 调整当前页
            if (pageItems.isEmpty()) {
                currentPage = 0;
            } else if (currentPage >= getTotalPages()) {
                currentPage = getTotalPages() - 1;
            }
        }
    }

    /**
     * 清空页面项目
     */
    public void clearPageItems() {
        pageItems.clear();
        currentPage = 0;
    }

    /**
     * 获取总页数
     */
    public int getTotalPages() {
        return pageItems.isEmpty() ? 1 : (int) Math.ceil((double) pageItems.size() / itemsPerPage);
    }

    /**
     * 获取当前页
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 获取页面项目列表
     */
    public List<GUIComponent> getPageItems() {
        return new ArrayList<>(pageItems);
    }
}