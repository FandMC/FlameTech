package cn.fandmc.flametech.gui.base;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有GUI的基类
 */
public abstract class BaseGUI implements InventoryHolder {

    protected final Main plugin;
    protected final String guiId;
    protected final String title;
    protected final int size;
    protected final Inventory inventory;
    protected final Map<Integer, GUIComponent> components = new HashMap<>();

    protected String parentGUIId;
    protected Player currentViewer;

    public BaseGUI(Main plugin, String guiId, int size, String title) {
        this.plugin = plugin;
        this.guiId = guiId;
        this.size = size;
        this.title = MessageUtils.colorize(title);
        this.inventory = Bukkit.createInventory(this, size, this.title);

        validateSize();
    }

    private void validateSize() {
        if (size <= 0 || size % 9 != 0 || size > 54) {
            throw new IllegalArgumentException("Invalid inventory size: " + size);
        }
    }

    /**
     * 设置组件到指定槽位
     */
    public void setComponent(int slot, GUIComponent component) {
        if (!isValidSlot(slot)) {
            MessageUtils.logWarning("Invalid slot " + slot + " for GUI " + guiId);
            return;
        }

        if (component != null) {
            components.put(slot, component);
            updateSlot(slot);
        } else {
            removeComponent(slot);
        }
    }

    /**
     * 移除指定槽位的组件
     */
    public void removeComponent(int slot) {
        if (isValidSlot(slot)) {
            components.remove(slot);
            inventory.clear(slot);
        }
    }

    /**
     * 清空所有组件
     */
    public void clearComponents() {
        components.clear();
        inventory.clear();
    }

    /**
     * 更新指定槽位的显示
     */
    protected void updateSlot(int slot) {
        GUIComponent component = components.get(slot);
        if (component != null && currentViewer != null) {
            if (component.isVisible(currentViewer)) {
                inventory.setItem(slot, component.getDisplayItem());
            } else {
                inventory.clear(slot);
            }
        }
    }

    /**
     * 刷新整个GUI
     */
    public void refresh() {
        if (currentViewer != null) {
            buildGUI(currentViewer);
            updateAllSlots();
        }
    }

    /**
     * 更新所有槽位
     */
    protected void updateAllSlots() {
        for (int slot : components.keySet()) {
            updateSlot(slot);
        }
    }

    /**
     * 打开GUI给指定玩家
     */
    public void open(Player player) {
        if (!ValidationUtils.isValidPlayer(player)) {
            MessageUtils.logWarning("Attempted to open GUI for invalid player");
            return;
        }

        try {
            this.currentViewer = player;
            buildGUI(player);
            updateAllSlots();
            player.openInventory(inventory);

            onOpen(player);

        } catch (Exception e) {
            MessageUtils.logError("Failed to open GUI " + guiId + " for player " + player.getName() + ": " + e.getMessage());
            if (plugin.isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理点击事件
     */
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // 取消事件，防止物品被拿走
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (!isValidSlot(slot)) {
            return;
        }

        GUIComponent component = components.get(slot);
        if (component != null && component.isClickable(player)) {
            try {
                component.onClick(player, event);
                onClick(player, event, component);
            } catch (Exception e) {
                MessageUtils.logError("Error handling click in GUI " + guiId + ": " + e.getMessage());
                if (plugin.isDebugMode()) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查槽位是否有效
     */
    protected boolean isValidSlot(int slot) {
        return slot >= 0 && slot < size;
    }

    /**
     * 设置父GUI
     */
    public void setParentGUI(String parentGUIId) {
        this.parentGUIId = parentGUIId;
    }

    /**
     * 获取父GUI ID
     */
    public String getParentGUIId() {
        return parentGUIId;
    }

    /**
     * 公开的关闭方法，供管理器调用
     */
    public void handleClose(Player player) {
        onClose(player);
    }

    // 抽象方法 - 子类必须实现

    /**
     * 构建GUI内容
     */
    protected abstract void buildGUI(Player player);

    // 可选的钩子方法

    /**
     * GUI打开时调用
     */
    protected void onOpen(Player player) {
        // 默认空实现
    }

    /**
     * GUI关闭时调用
     */
    protected void onClose(Player player) {
        // 默认空实现
    }

    /**
     * 组件被点击时调用
     */
    protected void onClick(Player player, InventoryClickEvent event, GUIComponent component) {
        // 默认空实现
    }

    // Getter方法

    public String getGuiId() {
        return guiId;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Player getCurrentViewer() {
        return currentViewer;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Main getPlugin() {
        return plugin;
    }
}
