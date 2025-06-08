package cn.fandmc.flametech.gui.impl.utils;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
import cn.fandmc.flametech.gui.base.PaginatedGUI;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.unlock.data.UnlockResult;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 材料相关配方列表GUI - 显示使用指定材料的所有配方
 */
public class MaterialRelatedRecipesGUI extends PaginatedGUI {

    private final Material material;
    private final List<Recipe> relatedRecipes;

    public MaterialRelatedRecipesGUI(Main plugin, Material material, List<Recipe> relatedRecipes, String parentGUIId) {
        super(plugin, "material_related_recipes_" + material.getMaterialId(),
                plugin.getConfigManager().getLang("gui.material_related_recipes.title")
                        .replace("%material%", material.getDisplayName())
                        .replace("%count%", String.valueOf(relatedRecipes.size())));

        this.material = material;
        this.relatedRecipes = new ArrayList<>(relatedRecipes);
        setParentGUI(parentGUIId);
    }

    @Override
    protected void buildGUI(Player player) {
        // 清空并重新构建页面项目
        clearPageItems();

        // 添加所有相关配方
        for (Recipe recipe : relatedRecipes) {
            addPageItem(new RelatedRecipeComponent(recipe, player));
        }

        // 调用父类方法构建GUI
        super.buildGUI(player);
    }

    @Override
    protected void buildExtraComponents(Player player) {
        // 返回按钮
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

        // 材料信息按钮
        setComponent(46, new MaterialInfoComponent());
    }

    /**
     * 相关配方组件
     */
    private class RelatedRecipeComponent implements GUIComponent {
        private final Recipe recipe;
        private final Player player;

        public RelatedRecipeComponent(Recipe recipe, Player player) {
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

                String displayName = plugin.getConfigManager().getLang("gui.material_related_recipes.recipe_locked_name")
                        .replace("%recipe%", recipe.getDisplayName());

                List<String> lore = new ArrayList<>();
                lore.add(plugin.getConfigManager().getLang("gui.material_related_recipes.recipe_locked_lore_exp")
                        .replace("%required%", String.valueOf(requiredExp)));
                lore.add("");

                // 显示材料在配方中的用途
                addMaterialUsageInfo(lore);

                lore.add("");
                lore.add(plugin.getConfigManager().getLang("gui.material_related_recipes.recipe_locked_lore_click"));

                return new ItemBuilder(org.bukkit.Material.BARRIER)
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
                    lore.add(plugin.getConfigManager().getLang("gui.material_related_recipes.recipe_type",
                            "%type%", recipe.getType().getDisplayName()));

                    // 显示材料在配方中的用途
                    addMaterialUsageInfo(lore);

                    lore.add("");
                    lore.add(plugin.getConfigManager().getLang("gui.material_related_recipes.click_view_recipe"));

                    meta.setLore(lore);
                    display.setItemMeta(meta);
                }
                return display;
            }
        }

        /**
         * 添加材料在配方中的用途信息
         */
        private void addMaterialUsageInfo(List<String> lore) {
            ItemStack materialItem = material.createItem();
            int totalUsed = 0;

            // 计算配方中使用的材料总数
            for (ItemStack ingredient : recipe.getIngredients().values()) {
                if (ingredient != null && isSameMaterial(ingredient, materialItem)) {
                    totalUsed += ingredient.getAmount();
                }
            }

            if (totalUsed > 0) {
                lore.add(plugin.getConfigManager().getLang("gui.material_related_recipes.material_usage",
                        "%material%", material.getDisplayName(),
                        "%amount%", String.valueOf(totalUsed)));
            }
        }

        /**
         * 检查两个物品是否为同一材料
         */
        private boolean isSameMaterial(ItemStack item1, ItemStack item2) {
            if (item1 == null || item2 == null) return false;

            if (item1.getType() != item2.getType()) return false;

            boolean isItem1FTMaterial = plugin.getMaterialManager().isMaterial(item1);
            boolean isItem2FTMaterial = plugin.getMaterialManager().isMaterial(item2);

            if (isItem1FTMaterial && isItem2FTMaterial) {
                var material1 = plugin.getMaterialManager().getMaterialFromStack(item1);
                var material2 = plugin.getMaterialManager().getMaterialFromStack(item2);

                if (material1.isPresent() && material2.isPresent()) {
                    return material1.get().getMaterialId().equals(material2.get().getMaterialId());
                }
            } else if (!isItem1FTMaterial && !isItem2FTMaterial) {
                return item1.isSimilar(item2);
            }

            return false;
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
                            MaterialRelatedRecipesGUI.this.open(player);
                        });
                    } else {
                        handleUnlockFailure(player, result);
                    }
                } else {
                    // 已解锁，打开配方详情
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        try {
                            ItemRecipeGUI recipeGUI = new ItemRecipeGUI(plugin, recipe, recipe.getMultiblockId());
                            recipeGUI.open(player);
                        } catch (Exception e) {
                            MessageUtils.logError("Failed to open recipe GUI: " + e.getMessage());
                            MessageUtils.sendMessage(player,
                                    plugin.getConfigManager().getLang("gui.material_related_recipes.error_open_recipe"));
                        }
                    });
                }
            } catch (Exception e) {
                MessageUtils.logError("Error in RelatedRecipeComponent onClick: " + e.getMessage());
                MessageUtils.sendMessage(player,
                        plugin.getConfigManager().getLang("gui.material_related_recipes.error_click_event"));
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
                    MessageUtils.sendMessage(player,
                            plugin.getConfigManager().getLang("gui.material_related_recipes.unlock_failed_default")
                                    .replace("%message%", result.getMessage()));
                    break;
            }
        }
    }

    /**
     * 材料信息组件
     */
    private class MaterialInfoComponent implements GUIComponent {
        @Override
        public ItemStack getDisplayItem() {
            ItemStack materialItem = material.createItem();

            if (materialItem.hasItemMeta()) {
                var meta = materialItem.getItemMeta();
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<>();

                lore.add("");
                lore.add(plugin.getConfigManager().getLang("gui.material_related_recipes.material_info.title"));
                lore.add(plugin.getConfigManager().getLang("gui.material_related_recipes.material_info.id",
                        "%id%", material.getMaterialId()));
                lore.add(plugin.getConfigManager().getLang("gui.material_related_recipes.material_info.usage_count",
                        "%count%", String.valueOf(relatedRecipes.size())));

                meta.setLore(lore);
                materialItem.setItemMeta(meta);
            }

            return materialItem;
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            // 材料信息不可点击，或者可以实现切换回材料查看器
            try {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    MaterialViewerGUI materialViewer = new MaterialViewerGUI(plugin, material, "materials");
                    materialViewer.open(player);
                });
            } catch (Exception e) {
                MessageUtils.logError("Error switching back to material viewer: " + e.getMessage());
            }
        }

        @Override
        public boolean isClickable(Player player) {
            return true;
        }
    }
}