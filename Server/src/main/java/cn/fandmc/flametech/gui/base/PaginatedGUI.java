package cn.fandmc.flametech.gui.base;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.gui.components.NavigationComponent;
import cn.fandmc.flametech.gui.components.StaticComponent;
import cn.fandmc.flametech.gui.components.UnlockableComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页GUI基类 - 最终修复版本
 *
 * 核心修复：翻页时只更新显示，不重新构建整个GUI
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
        this.contentSlots = contentSlots != null ? contentSlots : getDefaultContentSlots();
        this.itemsPerPage = this.contentSlots.length;
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
                                .displayName(plugin.getConfigManager().getLang(Messages.GUI_COMMON_NO_PREVIOUS_PAGE))
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

        // 页面信息 - 动态更新
        setComponent(pageInfoSlot, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                return ItemBuilder.createPageInfoItem(currentPage + 1, getTotalPages(), pageItems.size());
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                // 页面信息不可点击
            }

            @Override
            public boolean isClickable(Player player) {
                return false;
            }
        });

        // 下一页按钮 - 修复版本
        setComponent(nextPageSlot, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                return hasNextPage() ?
                        ItemBuilder.createNextPageButton() :
                        new ItemBuilder(org.bukkit.Material.GRAY_STAINED_GLASS_PANE)
                                .displayName(plugin.getConfigManager().getLang(Messages.GUI_COMMON_NO_NEXT_PAGE))
                                .build();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                if (hasNextPage()) {
                    nextPage(player);
                }
            }

            @Override
            public boolean isClickable(Player player) {
                return hasNextPage();
            }
        });
    }

    /**
     * 检查是否有下一页 - 修复版本
     */
    private boolean hasNextPage() {
        if (pageItems.isEmpty()) {
            return false;
        }

        int totalPages = getTotalPages();
        return totalPages > 1 && currentPage < (totalPages - 1);
    }

    protected void displayCurrentPage(Player player) {
        // 清空内容槽位
        for (int slot : contentSlots) {
            removeComponent(slot);
        }

        if (pageItems.isEmpty()) {
            MessageUtils.logDebug("PaginatedGUI: 没有页面项目可显示");
            return;
        }

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, pageItems.size());

        MessageUtils.logDebug("PaginatedGUI: 显示项目 " + startIndex + " 到 " + (endIndex - 1) +
                " (当前页=" + currentPage + ", 每页=" + itemsPerPage + ")");

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
     * 下一页 - 修复版本：只更新显示，不重新构建GUI
     */
    public void nextPage(Player player) {
        if (hasNextPage()) {
            currentPage++;
            MessageUtils.logDebug("PaginatedGUI: 切换到下一页 " + currentPage);

            // 只更新页面显示，不重新构建整个GUI
            updatePageDisplay(player);
        } else {
            MessageUtils.logDebug("PaginatedGUI: 已经是最后一页，无法前进");
        }
    }

    /**
     * 上一页 - 修复版本：只更新显示，不重新构建GUI
     */
    public void previousPage(Player player) {
        if (currentPage > 0) {
            currentPage--;
            MessageUtils.logDebug("PaginatedGUI: 切换到上一页 " + currentPage);

            // 只更新页面显示，不重新构建整个GUI
            updatePageDisplay(player);
        } else {
            MessageUtils.logDebug("PaginatedGUI: 已经是第一页，无法后退");
        }
    }

    /**
     * 更新页面显示 - 新方法：只更新必要的部分
     */
    private void updatePageDisplay(Player player) {
        // 1. 更新内容显示
        displayCurrentPage(player);

        // 2. 更新控制按钮的显示状态
        updateControlButtons(player);
    }

    /**
     * 更新控制按钮的显示状态
     */
    private void updateControlButtons(Player player) {
        try {
            // 更新上一页按钮
            GUIComponent prevButton = components.get(previousPageSlot);
            if (prevButton != null) {
                updateSlot(previousPageSlot);
            }

            // 更新页面信息
            GUIComponent pageInfo = components.get(pageInfoSlot);
            if (pageInfo != null) {
                updateSlot(pageInfoSlot);
            }

            // 更新下一页按钮
            GUIComponent nextButton = components.get(nextPageSlot);
            if (nextButton != null) {
                updateSlot(nextPageSlot);
            }

        } catch (Exception e) {
            MessageUtils.logError("更新控制按钮时发生错误: " + e.getMessage());
        }
    }

    /**
     * 跳转到指定页 - 修复版本
     */
    public void goToPage(int page, Player player) {
        int totalPages = getTotalPages();
        if (page >= 0 && page < totalPages) {
            currentPage = page;
            MessageUtils.logDebug("PaginatedGUI: 跳转到页面 " + currentPage);

            // 只更新页面显示，不重新构建整个GUI
            updatePageDisplay(player);
        } else {
            MessageUtils.logDebug("PaginatedGUI: 页面 " + page + " 超出范围 (0-" + (totalPages - 1) + ")");
        }
    }

    /**
     * 添加页面项目
     */
    public void addPageItem(GUIComponent item) {
        if (item != null) {
            pageItems.add(item);
            MessageUtils.logDebug("PaginatedGUI: 添加页面项目，现在总数: " + pageItems.size());
        }
    }

    /**
     * 移除页面项目
     */
    public void removePageItem(int index) {
        if (index >= 0 && index < pageItems.size()) {
            pageItems.remove(index);

            // 调整当前页 - 修复版本
            int totalPages = getTotalPages();
            if (totalPages == 0) {
                currentPage = 0;
            } else if (currentPage >= totalPages) {
                currentPage = Math.max(0, totalPages - 1);
            }

            MessageUtils.logDebug("PaginatedGUI: 移除项目后，当前页=" + currentPage + ", 总页数=" + totalPages);
        }
    }

    /**
     * 清空页面项目
     */
    public void clearPageItems() {
        pageItems.clear();
        currentPage = 0;
        MessageUtils.logDebug("PaginatedGUI: 清空所有页面项目");
    }

    /**
     * 获取总页数 - 修复版本
     */
    public int getTotalPages() {
        if (pageItems.isEmpty() || itemsPerPage <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) pageItems.size() / itemsPerPage);
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

    /**
     * 检查当前页是否有效
     */
    public boolean isCurrentPageValid() {
        int totalPages = getTotalPages();
        return currentPage >= 0 && currentPage < totalPages;
    }

    /**
     * 修正当前页到有效范围
     */
    public void fixCurrentPage() {
        int totalPages = getTotalPages();
        if (totalPages == 0) {
            currentPage = 0;
        } else if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        } else if (currentPage < 0) {
            currentPage = 0;
        }

        MessageUtils.logDebug("PaginatedGUI: 修正当前页为 " + currentPage);
    }

    /**
     * 刷新GUI - 修复版本：只在必要时重新构建
     */
    @Override
    public void refresh() {
        // 先修正当前页
        fixCurrentPage();

        // 如果当前有观察者，只更新显示
        if (currentViewer != null) {
            updatePageDisplay(currentViewer);
        }
    }

    /**
     * 完全重新构建GUI（用于强制刷新）
     */
    public void forceRefresh() {
        // 先修正当前页
        fixCurrentPage();

        // 然后完全重新构建
        super.refresh();
    }
}