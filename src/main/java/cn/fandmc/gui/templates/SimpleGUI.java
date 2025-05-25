package cn.fandmc.gui.templates;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.StaticItem;
import cn.fandmc.gui.components.UnlockableButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class SimpleGUI extends GUI {
    private int nextAutoSlot = 0;
    private final Set<Integer> manuallySetSlots = new HashSet<>();
    private Player currentPlayer; // 当前打开GUI的玩家

    public SimpleGUI(Main plugin, String name, int size, String title) {
        super(plugin, name, size, title);
    }

    @Override
    public void open(Player player) {
        this.currentPlayer = player;
        super.open(player);
    }

    protected void setBorder(Material material) {
        ItemStack borderItem = new ItemStack(material);
        ItemMeta meta = borderItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            borderItem.setItemMeta(meta);
        }

        for (int i = 0; i < 9; i++) {
            setComponent(i, new StaticItem(borderItem));
            if (size > 9) {
                setComponent(size - 9 + i, new StaticItem(borderItem));
            }
        }

        for (int row = 1; row < (size / 9) - 1; row++) {
            setComponent(row * 9, new StaticItem(borderItem));
            setComponent(row * 9 + 8, new StaticItem(borderItem));
        }
    }

    /**
     * 自动添加组件到下一个可用的中心位置
     */
    protected void setCenterItem(GUIComponent component) {
        int slot = findNextAvailableCenterSlot();
        if (slot != -1) {
            // 如果是可解锁按钮且有当前玩家，则包装为玩家特定组件
            if (component instanceof UnlockableButton && currentPlayer != null) {
                setComponent(slot, new PlayerSpecificUnlockableComponent((UnlockableButton) component, currentPlayer));
            } else {
                setComponent(slot, component);
            }
            manuallySetSlots.add(getRelativeSlot(slot));
        }
    }

    /**
     * 设置组件到指定的相对位置
     */
    protected void setCenterItem(int relativeSlot, GUIComponent component) {
        int actualSlot = getActualSlot(relativeSlot);
        if (actualSlot != -1) {
            if (component instanceof UnlockableButton && currentPlayer != null) {
                setComponent(actualSlot, new PlayerSpecificUnlockableComponent((UnlockableButton) component, currentPlayer));
            } else {
                setComponent(actualSlot, component);
            }
            manuallySetSlots.add(relativeSlot);
        }
    }

    /**
     * 查找下一个可用的中心位置
     */
    private int findNextAvailableCenterSlot() {
        int maxSlots = getCenterSlotsCount();

        while (nextAutoSlot < maxSlots) {
            if (!manuallySetSlots.contains(nextAutoSlot)) {
                int slot = nextAutoSlot;
                nextAutoSlot++;
                return getActualSlot(slot);
            }
            nextAutoSlot++;
        }

        return -1;
    }

    /**
     * 根据GUI大小获取实际的槽位
     */
    private int getActualSlot(int relativeSlot) {
        int[] centerSlots = getCenterSlots();
        if (relativeSlot >= 0 && relativeSlot < centerSlots.length) {
            return centerSlots[relativeSlot];
        }
        return -1;
    }

    /**
     * 根据实际槽位获取相对位置
     */
    private int getRelativeSlot(int actualSlot) {
        int[] centerSlots = getCenterSlots();
        for (int i = 0; i < centerSlots.length; i++) {
            if (centerSlots[i] == actualSlot) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取中心槽位数组
     */
    private int[] getCenterSlots() {
        if (size == 27) {
            return new int[]{10, 11, 12, 13, 14, 15, 16};
        } else if (size == 54) {
            return new int[]{
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34,
                    37, 38, 39, 40, 41, 42, 43
            };
        }
        return new int[0];
    }

    /**
     * 获取中心槽位总数
     */
    private int getCenterSlotsCount() {
        return getCenterSlots().length;
    }

    /**
     * 获取当前玩家
     */
    protected Player getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void clearComponents() {
        super.clearComponents();
        manuallySetSlots.clear();
        nextAutoSlot = 0;
    }

    /**
     * 内部类：为可解锁按钮提供玩家特定的显示和行为
     */
    private static class PlayerSpecificUnlockableComponent implements GUIComponent {
        private final UnlockableButton unlockableButton;
        private final Player player;

        public PlayerSpecificUnlockableComponent(UnlockableButton unlockableButton, Player player) {
            this.unlockableButton = unlockableButton;
            this.player = player;
        }

        @Override
        public ItemStack item() {
            // 根据玩家状态返回正确的显示
            return unlockableButton.getItemForPlayer(player);
        }

        @Override
        public void onClick(Player clickPlayer, InventoryClickEvent event) {
            // 委托给原始按钮处理
            unlockableButton.onClick(clickPlayer, event);
        }
    }
}