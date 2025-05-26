package cn.fandmc.flametech.gui.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.PaginatedGUI;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.gui.components.NavigationComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.unlock.data.UnlockResult;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
                plugin.getConfigManager().getLang("gui.structure_recipes.title")
                        .replace("%machine%", plugin.getConfigManager().getLang("multiblock." + multiblockId + ".name")));
        this.multiblockId = multiblockId;
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
                    .displayName(plugin.getConfigManager().getLang("gui.structure_recipes.no_recipes"))
                    .lore(plugin.getConfigManager().getStringList("gui.structure_recipes.no_recipes_lore"))
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
                return new ItemBuilder(Material.BARRIER)
                        .displayName("&c" + recipe.getDisplayName() + " &7(未解锁)")
                        .lore(
                                "&7需要经验等级: &e" + plugin.getUnlockManager().getRequiredExp(recipeUnlockId),
                                "",
                                "&e点击解锁"
                        )
                        .build();
            } else {
                // 已解锁状态
                ItemStack display = recipe.getResult().clone();
                if (display.hasItemMeta()) {
                    var meta = display.getItemMeta();
                    var lore = meta.getLore();
                    if (lore == null) lore = new java.util.ArrayList<>();

                    lore.add("");
                    lore.add(plugin.getConfigManager().getLang("gui.structure_recipes.click_view_recipe"));

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
                        player.sendMessage(plugin.getConfigManager().getLang("unlock.success")
                                .replace("%item%", recipe.getDisplayName()));

                        // 刷新界面
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            StructureRecipesGUI.this.open(player);
                        });
                    } else {
                        // 处理解锁失败
                        switch (result.getStatus()) {
                            case INSUFFICIENT_EXP:
                                player.sendMessage(plugin.getConfigManager().getLang("unlock.insufficient_exp")
                                        .replace("%required%", String.valueOf(result.getRequiredExp())));
                                break;
                            case ALREADY_UNLOCKED:
                                player.sendMessage(plugin.getConfigManager().getLang("unlock.already_unlocked"));
                                break;
                            default:
                                player.sendMessage("§c解锁失败: " + result.getMessage());
                                break;
                        }
                    }
                } else {
                    // 已解锁，打开配方详情
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        try {
                            ItemRecipeGUI recipeGUI = new ItemRecipeGUI(plugin, recipe, multiblockId);
                            recipeGUI.open(player);
                        } catch (Exception e) {
                            MessageUtils.logError("Failed to open recipe GUI: " + e.getMessage());
                            MessageUtils.sendMessage(player, "&c打开配方详情时发生错误");
                        }
                    });
                }
            } catch (Exception e) {
                MessageUtils.logError("Error in RecipeComponent onClick: " + e.getMessage());
                MessageUtils.sendMessage(player, "&c处理点击事件时发生错误");
            }
        }
    }
}