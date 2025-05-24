package cn.fandmc.gui.templates;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.StaticItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public abstract class SimpleGUI extends GUI {
    private int nextAutoSlot = 0;
    private final Set<Integer> manuallySetSlots = new HashSet<>();

    public SimpleGUI(Main plugin, String name, int size, String title) {
        super(plugin, name, size, title);
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

    protected void setCenterItem(GUIComponent component) {
        int slot = findNextAvailableCenterSlot();
        if (slot != -1) {
            setCenterItem(slot, component);
        }
    }

    protected void setCenterItem(int relativeSlot, GUIComponent component) {
        if (size == 27) {
            int[] centerSlots = {10, 11, 12, 13, 14, 15, 16};
            if (relativeSlot >= 0 && relativeSlot < centerSlots.length) {
                int actualSlot = centerSlots[relativeSlot];
                setComponent(actualSlot, component);
                manuallySetSlots.add(relativeSlot);
            }
        } else if (size == 54) {
            int[] centerSlots = {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34,
                    37, 38, 39, 40, 41, 42, 43
            };
            if (relativeSlot >= 0 && relativeSlot < centerSlots.length) {
                int actualSlot = centerSlots[relativeSlot];
                setComponent(actualSlot, component);
                manuallySetSlots.add(relativeSlot);
            }
        }
    }

    private int findNextAvailableCenterSlot() {
        int maxSlots = (size == 27) ? 7 : 28;

        while (nextAutoSlot < maxSlots) {
            if (!manuallySetSlots.contains(nextAutoSlot)) {
                int slot = nextAutoSlot;
                nextAutoSlot++;
                return slot;
            }
            nextAutoSlot++;
        }

        return -1;
    }

    @Override
    public void clearComponents() {
        super.clearComponents();
        manuallySetSlots.clear();
        nextAutoSlot = 0;
    }
}