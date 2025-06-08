package cn.fandmc.flametech.gui.impl.utils;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.gui.base.BaseGUI;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.gui.components.StaticComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.multiblock.base.MultiblockStructure;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.recipes.base.ShapedRecipe;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 物品配方GUI
 */
public class ItemRecipeGUI extends BaseGUI {

    private final Recipe recipe;
    private final String fromMultiblock;

    private static final int[] RECIPE_SLOTS = {5, 6, 7, 14, 15, 16, 23, 24, 25};
    private static final int TYPE_SLOT = 11;
    private static final int RESULT_SLOT = 8;
    private static final int BACK_SLOT = 18;

    public ItemRecipeGUI(Main plugin, Recipe recipe, String fromMultiblock) {
        super(plugin, "item_recipe_" + recipe.getRecipeId(), 27,
                plugin.getConfigManager().getLang("gui.item_recipe.title")
                        .replace("%item%", recipe.getDisplayName()));
        this.recipe = recipe;
        this.fromMultiblock = fromMultiblock;
    }

    @Override
    protected void buildGUI(Player player) {
        clearComponents();

        // 不设置边框，让配方显示更清晰

        // 显示配方
        displayRecipe();

        // 配方类型指示器
        setComponent(TYPE_SLOT, new StaticComponent(createTypeIndicator()));

        // 结果物品
        setComponent(RESULT_SLOT, new StaticComponent(createResultItem()));

        // 返回按钮
        setComponent(BACK_SLOT, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                return ItemBuilder.createBackButton();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                try {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.closeInventory();
                    });
                } catch (Exception e) {
                    MessageUtils.logError("Error in back button: " + e.getMessage());
                    player.closeInventory();
                }
            }
        });
    }

    private void displayRecipe() {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            displayShapedRecipe(shapedRecipe);
        } else {
            displayGenericRecipe();
        }
    }

    private void displayShapedRecipe(ShapedRecipe shaped) {
        String[] pattern = shaped.getPattern();
        Map<Character, ItemStack> ingredients = shaped.getIngredientMap();

        // 清空配方槽位
        for (int slot : RECIPE_SLOTS) {
            removeComponent(slot);
        }

        // 显示有序配方
        for (int row = 0; row < Math.min(3, pattern.length); row++) {
            for (int col = 0; col < Math.min(3, pattern[row].length()); col++) {
                char c = pattern[row].charAt(col);
                if (c != ' ' && ingredients.containsKey(c)) {
                    ItemStack ingredient = ingredients.get(c).clone();
                    addIngredientLore(ingredient);

                    int slotIndex = row * 3 + col;
                    setComponent(RECIPE_SLOTS[slotIndex], new StaticComponent(ingredient));
                }
            }
        }
    }

    private void displayGenericRecipe() {
        Map<Integer, ItemStack> ingredients = recipe.getIngredients();

        for (Map.Entry<Integer, ItemStack> entry : ingredients.entrySet()) {
            int slot = entry.getKey();
            ItemStack ingredient = entry.getValue().clone();

            if (slot < RECIPE_SLOTS.length) {
                addIngredientLore(ingredient);
                setComponent(RECIPE_SLOTS[slot], new StaticComponent(ingredient));
            }
        }
    }

    private void addIngredientLore(ItemStack ingredient) {
        if (ingredient != null && ingredient.hasItemMeta()) {
            ItemMeta meta = ingredient.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();

            lore.add("");
            lore.add(plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_INGREDIENT_AMOUNT,
                    "%amount%", String.valueOf(ingredient.getAmount())));

            meta.setLore(lore);
            ingredient.setItemMeta(meta);
        }
    }

    private ItemStack createTypeIndicator() {
        // 获取多方块结构的显示名称
        String multiblockDisplayName = getMultiblockDisplayName(fromMultiblock);

        return new ItemBuilder(Material.CRAFTING_TABLE)
                .displayName(plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_TYPE_INDICATOR_NAME))
                .lore(
                        plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_TYPE_INDICATOR_TYPE,
                                "%type%", recipe.getType().getDisplayName()),
                        plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_TYPE_INDICATOR_SOURCE,
                                "%source%", multiblockDisplayName),
                        "",
                        plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_TYPE_INDICATOR_USAGE_LINE1),
                        plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_TYPE_INDICATOR_USAGE_LINE2)
                )
                .build();
    }

    /**
     * 获取多方块结构的显示名称
     */
    private String getMultiblockDisplayName(String multiblockId) {
        try {
            // 首先尝试从语言配置获取
            String langKey = "multiblock." + multiblockId + ".name";
            String displayName = plugin.getConfigManager().getLang(langKey);

            // 如果配置中没有，尝试从结构管理器获取
            if (displayName.contains("未找到语言键")) {
                Optional<MultiblockStructure> structureOpt = plugin.getMultiblockManager().getStructure(multiblockId);
                if (structureOpt.isPresent()) {
                    return structureOpt.get().getDisplayName();
                }

                // 如果都没有，返回格式化的ID
                return formatMultiblockId(multiblockId);
            }

            return displayName;
        } catch (Exception e) {
            MessageUtils.logError("Error getting multiblock display name: " + e.getMessage());
            return formatMultiblockId(multiblockId);
        }
    }

    /**
     * 格式化多方块ID为可读名称
     */
    private String formatMultiblockId(String multiblockId) {
        if (multiblockId == null || multiblockId.isEmpty()) {
            return plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_UNKNOWN_STRUCTURE);
        }

        // 移除前缀并格式化
        String formatted = multiblockId.replace("enhanced_crafting_table",
                        plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_ENHANCED_CRAFTING_TABLE))
                .replace("_", " ");

        // 首字母大写
        if (!formatted.isEmpty()) {
            formatted = formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        }

        return formatted;
    }

    private ItemStack createResultItem() {
        ItemStack resultItem = recipe.getResult().clone();
        if (resultItem.hasItemMeta()) {
            ItemMeta meta = resultItem.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();

            lore.add("");
            lore.add(plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_RESULT_TITLE));
            lore.add(plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_RESULT_AMOUNT,
                    "%amount%", String.valueOf(resultItem.getAmount())));
            lore.add("");
            lore.add(plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_RESULT_INSTRUCTION_LINE1));
            lore.add(plugin.getConfigManager().getLang(Messages.GUI_ITEM_RECIPE_RESULT_INSTRUCTION_LINE2,
                    "%machine%", getMultiblockDisplayName(fromMultiblock)));

            meta.setLore(lore);
            resultItem.setItemMeta(meta);
        }
        return resultItem;
    }
}