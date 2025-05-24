package cn.fandmc.machines.basic;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.StaticItem;
import cn.fandmc.recipe.Recipe;
import cn.fandmc.recipe.RecipeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EnhancedCraftingGUI extends GUI {
    private final Inventory dispenserInventory;

    public EnhancedCraftingGUI(Main plugin) {
        super(plugin, "enhanced_crafting", 54,
                plugin.getConfigManager().getLang("gui.enhanced_crafting.title"));
        this.dispenserInventory = null;
        GUIManager.registerGUI(this);
    }

    public EnhancedCraftingGUI(Main plugin, Inventory dispenserInventory) {
        super(plugin, "enhanced_crafting_instance", 54,
                plugin.getConfigManager().getLang("gui.enhanced_crafting.title"));
        this.dispenserInventory = dispenserInventory;
    }

    @Override
    protected void buildGUI() {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }

        for (int i = 0; i < 9; i++) {
            setComponent(i, new StaticItem(border));
            setComponent(45 + i, new StaticItem(border));
        }

        for (int i = 1; i < 5; i++) {
            setComponent(i * 9, new StaticItem(border));
            setComponent(i * 9 + 8, new StaticItem(border));
        }

        if (dispenserInventory != null) {
            displayDispenserItems();
        }

        setComponent(24, new GUIComponent() {
            @Override
            public ItemStack item() {
                return null;
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                ItemStack output = event.getCurrentItem();
                if (output != null && output.getType() != Material.AIR) {
                    if (consumeIngredients()) {
                        if (player.getInventory().addItem(output).isEmpty()) {
                            event.getClickedInventory().setItem(24, null);
                            refresh();
                            player.sendMessage(plugin.getConfigManager()
                                    .getLang("gui.enhanced_crafting.craft_success"));
                        } else {
                            player.sendMessage(plugin.getConfigManager()
                                    .getLang("gui.enhanced_crafting.inventory_full"));
                        }
                    }
                }
            }
        });

        setComponent(23, new StaticItem(createCraftInfoItem()));

        setComponent(49, new GUIComponent() {
            @Override
            public ItemStack item() {
                return createBackButton();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                player.closeInventory();
            }
        });

        updateOutput();
    }

    private void displayDispenserItems() {
        if (dispenserInventory == null) return;

        int[] displaySlots = {
                10, 11, 12,
                19, 20, 21,
                28, 29, 30
        };

        for (int i = 0; i < 9 && i < dispenserInventory.getSize(); i++) {
            ItemStack item = dispenserInventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                setComponent(displaySlots[i], new StaticItem(item.clone()));
            }
        }
    }

    private void updateOutput() {
        if (dispenserInventory == null) return;

        Map<Integer, ItemStack> inputs = getInputsFromDispenser();
        Recipe recipe = RecipeManager.getInstance()
                .findMatchingRecipe("enhanced_crafting_table", inputs);

        if (recipe != null) {
            setComponent(24, new StaticItem(recipe.getResult()));
        } else {
            removeComponent(24);
        }
    }

    private Map<Integer, ItemStack> getInputsFromDispenser() {
        Map<Integer, ItemStack> inputs = new HashMap<>();

        if (dispenserInventory != null) {
            for (int i = 0; i < 9 && i < dispenserInventory.getSize(); i++) {
                ItemStack item = dispenserInventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    inputs.put(i, item);
                }
            }
        }

        return inputs;
    }

    private boolean consumeIngredients() {
        if (dispenserInventory == null) return false;

        Map<Integer, ItemStack> inputs = getInputsFromDispenser();
        Recipe recipe = RecipeManager.getInstance()
                .findMatchingRecipe("enhanced_crafting_table", inputs);

        if (recipe == null) return false;

        Map<Integer, ItemStack> required = recipe.getIngredients();

        for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) {
            int slot = entry.getKey();
            ItemStack requiredItem = entry.getValue();

            if (slot < dispenserInventory.getSize()) {
                ItemStack current = dispenserInventory.getItem(slot);
                if (current != null) {
                    current.setAmount(current.getAmount() - requiredItem.getAmount());
                    if (current.getAmount() <= 0) {
                        dispenserInventory.setItem(slot, null);
                    }
                }
            }
        }

        return true;
    }

    private ItemStack createCraftInfoItem() {
        ItemStack item = new ItemStack(Material.ANVIL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().getLang("gui.enhanced_crafting.info.name"));
            meta.setLore(plugin.getConfigManager().getStringList("gui.enhanced_crafting.info.lore"));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().getLang("gui.enhanced_crafting.back"));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void refresh() {
        buildGUI();
        super.refresh();
    }
}