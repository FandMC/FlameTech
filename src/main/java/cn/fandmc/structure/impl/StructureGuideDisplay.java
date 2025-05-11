// cn.fandmc.structure.impl.StructureGuideDisplay.java
package cn.fandmc.structure.impl;

import cn.fandmc.structure.Structure;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StructureGuideDisplay {
    public static void showStructureGuide(Inventory inv, Structure structure) {
        int slot = 10;
        for (int[] pos : structure.getStructureLayout()) {
            Material block = pos[0] == 0 ? Material.CRAFTING_TABLE : Material.DISPENSER;
            inv.setItem(pos[1], new ItemStack(block));
        }
    }
}
