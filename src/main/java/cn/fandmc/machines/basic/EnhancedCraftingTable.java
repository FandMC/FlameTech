package cn.fandmc.machines.basic;

import cn.fandmc.Main;
import cn.fandmc.multiblock.BlockOffset;
import cn.fandmc.multiblock.MultiblockStructure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnhancedCraftingTable extends MultiblockStructure {

    public EnhancedCraftingTable() {
        super("enhanced_crafting_table",
                Main.getInstance().getConfigManager().getLang("multiblock.enhanced_crafting_table.name"),
                createStructure());
    }

    private static Map<BlockOffset, Material> createStructure() {
        Map<BlockOffset, Material> structure = new HashMap<>();

        structure.put(new BlockOffset(0, 0, 0), Material.CRAFTING_TABLE);
        structure.put(new BlockOffset(0, -1, 0), Material.DISPENSER);

        return structure;
    }

    @Override
    public void onActivate(Player player, Location location) {
        Block dispenserBlock = location.clone().add(0, -1, 0).getBlock();

        if (dispenserBlock.getType() != Material.DISPENSER) {
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.error"));
            return;
        }

        Dispenser dispenser = (Dispenser) dispenserBlock.getState();
        Inventory dispenserInv = dispenser.getInventory();

        boolean hasItems = false;
        for (ItemStack item : dispenserInv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                hasItems = true;
                break;
            }
        }

        if (!hasItems) {
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.created"));
            player.sendMessage(Main.getInstance().getConfigManager()
                    .getLang("multiblock.enhanced_crafting_table.hint"));
        } else {
            EnhancedCraftingGUI gui = new EnhancedCraftingGUI(Main.getInstance(), dispenserInv);
            gui.open(player);
        }
    }

    @Override
    public boolean canCraft(String recipeId) {
        return true;
    }
}