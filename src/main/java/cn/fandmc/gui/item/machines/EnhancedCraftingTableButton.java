package cn.fandmc.gui.item.machines;

import cn.fandmc.Main;
import cn.fandmc.gui.components.UnlockableButton;
import cn.fandmc.gui.impl.RecipeViewerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnhancedCraftingTableButton extends UnlockableButton {
    private final String multiblockId = "enhanced_crafting_table";

    public EnhancedCraftingTableButton() {
        super("multiblock.enhanced_crafting_table", "gui.basic_machines.enhanced_crafting.name");
    }

    @Override
    protected ItemStack createUnlockedDisplay() {
        ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Main plugin = Main.getInstance();
            meta.setDisplayName(plugin.getConfigManager().getLang(getDisplayNameKey()));
            meta.setLore(plugin.getConfigManager().getStringList("gui.basic_machines.enhanced_crafting.lore"));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    protected void onAlreadyUnlocked(Player player, InventoryClickEvent event) {
        // 已解锁时打开配方查看界面
        RecipeViewerGUI viewerGUI = RecipeViewerGUI.getInstance(Main.getInstance(), multiblockId);
        viewerGUI.open(player);
    }
}