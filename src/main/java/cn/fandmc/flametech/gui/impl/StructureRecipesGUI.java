package cn.fandmc.flametech.gui.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.gui.base.PaginatedGUI;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.unlock.data.UnlockResult;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结构配方列表GUI
 */
public class StructureRecipesGUI extends PaginatedGUI {

    private final String multiblockId;
    private static final Map<String, StructureRecipesGUI> instances = new HashMap<>();

    private StructureRecipesGUI(Main plugin, String multiblockId) {
        super(plugin, "structure_recipes_" + multiblockId,
                plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_RECIPES_TITLE)
                        .replace("%machine%", getMultiblockDisplayName(plugin, multiblockId)));
        this.multiblockId = multiblockId;
    }

    /**
     * 获取多方块结构的显示名称
     */
    private static String getMultiblockDisplayName(Main plugin, String multiblockId) {
        String langKey = "multiblock." + multiblockId + ".name";
        return plugin.getConfigManager().getLang(langKey);
    }

    public static StructureRecipesGUI getInstance(Main plugin, String multiblockId) {
        return instances.computeIfAbsent(multiblockId, k -> {
            StructureRecipesGUI gui = new StructureRecipesGUI(plugin, multiblockId);
            plugin.getGuiManager().registerGUI(gui);
            return gui;
        });
    }

    @Override
    protected void buildGUI(Player player) {
        loadRecipesForPlayer(player);
        super.buildGUI(player);
    }

    @Override
    protected void buildExtraComponents(Player player) {
        // 添加返回按钮
        if (getParentGUIId() != null) {
            setComponent(45, new GUIComponent() {
                @Override
                public ItemStack getDisplayItem() {
                    return ItemBuilder.createBackButton();
                }

                @Override
                public void onClick(Player player, InventoryClickEvent event) {
                    try {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            plugin.getGuiManager().openGUI(player, getParentGUIId());
                        });
                    } catch (Exception e) {
                        MessageUtils.logError("Error in back button: " + e.getMessage());
                        player.closeInventory();
                    }
                }
            });
        }
    }

    private void loadRecipesForPlayer(Player player) {
        clearPageItems();
        List<Recipe> recipes = plugin.getRecipeManager().getRecipesForMultiblock(multiblockId);

        if (recipes.isEmpty()) {
            addPageItem(new NoRecipeComponent());
        } else {
            for (Recipe recipe : recipes) {
                addPageItem(new RecipeComponent(recipe, player));
            }
        }
    }

    /**
     * 无配方组件
     */
    private class NoRecipeComponent implements GUIComponent {
        @Override
        public ItemStack getDisplayItem() {
            return new ItemBuilder(Material.BARRIER)
                    .displayName(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_RECIPES_NO_RECIPES))
                    .lore(plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_RECIPES_NO_RECIPES_LORE))
                    .build();
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            // 无操作
        }
    }

    /**
     * 配方组件
     */
    private class RecipeComponent implements GUIComponent {
        private final Recipe recipe;
        private final Player player;

        public RecipeComponent(Recipe recipe, Player player) {
            this.recipe = recipe;
            this.player = player;
        }

        @Override
        public ItemStack getDisplayItem() {
            String recipeUnlockId = "recipe." + recipe.getRecipeId();
            boolean isUnlocked = plugin.getUnlockManager().isUnlocked(player, recipeUnlockId);

            if (!isUnlocked) {
                // 未解锁状态
                int requiredExp = plugin.getUnlockManager().getRequiredExp(recipeUnlockId);

                String displayName = plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_RECIPES_RECIPE_LOCKED_NAME)
                        .replace("%recipe%", recipe.getDisplayName());

                List<String> lore = new ArrayList<>();
                lore.add(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_RECIPES_RECIPE_LOCKED_LORE_EXP)
                        .replace("%required%", String.valueOf(requiredExp)));
                lore.add("");
                lore.add(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_RECIPES_RECIPE_LOCKED_LORE_CLICK));

                return new ItemBuilder(Material.BARRIER)
                        .displayName(displayName)
                        .lore(lore)
                        .build();
            } else {
                // 已解锁状态
                ItemStack display = recipe.getResult().clone();
                if (display.hasItemMeta()) {
                    var meta = display.getItemMeta();
                    var lore = meta.getLore();
                    if (lore == null) lore = new ArrayList<>();

                    lore.add("");
                    lore.add(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_RECIPES_CLICK_VIEW_RECIPE));

                    meta.setLore(lore);
                    display.setItemMeta(meta);
                }
                return display;
            }
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            try {
                String recipeUnlockId = "recipe." + recipe.getRecipeId();

                if (!plugin.getUnlockManager().isUnlocked(player, recipeUnlockId)) {
                    // 尝试解锁配方
                    UnlockResult result = plugin.getUnlockManager().unlock(player, recipeUnlockId);

                    if (result.isSuccess()) {
                        MessageUtils.sendLocalizedMessage(player, Messages.UNLOCK_SUCCESS,
                                "%item%", recipe.getDisplayName());

                        // 刷新界面
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            StructureRecipesGUI.this.open(player);
                        });
                    } else {
                        // 处理解锁失败
                        handleUnlockFailure(player, result);
                    }
                } else {
                    // 已解锁，打开配方详情
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        try {
                            ItemRecipeGUI recipeGUI = new ItemRecipeGUI(plugin, recipe, multiblockId);
                            recipeGUI.open(player);
                        } catch (Exception e) {
                            MessageUtils.logError("Failed to open recipe GUI: " + e.getMessage());
                            MessageUtils.sendLocalizedMessage(player, Messages.GUI_STRUCTURE_RECIPES_ERROR_OPEN_RECIPE);
                        }
                    });
                }
            } catch (Exception e) {
                MessageUtils.logError("Error in RecipeComponent onClick: " + e.getMessage());
                MessageUtils.sendLocalizedMessage(player, Messages.GUI_STRUCTURE_RECIPES_ERROR_CLICK_EVENT);
            }
        }

        /**
         * 处理解锁失败的情况
         */
        private void handleUnlockFailure(Player player, UnlockResult result) {
            switch (result.getStatus()) {
                case INSUFFICIENT_EXP:
                    MessageUtils.sendLocalizedMessage(player, Messages.UNLOCK_INSUFFICIENT_EXP,
                            "%required%", String.valueOf(result.getRequiredExp()));
                    break;
                case ALREADY_UNLOCKED:
                    MessageUtils.sendLocalizedMessage(player, Messages.UNLOCK_ALREADY_UNLOCKED);
                    break;
                default:
                    MessageUtils.sendLocalizedMessage(player, Messages.GUI_STRUCTURE_RECIPES_UNLOCK_FAILED_DEFAULT,
                            "%message%", result.getMessage());
                    break;
            }
        }
    }
}