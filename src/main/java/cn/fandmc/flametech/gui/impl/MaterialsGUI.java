package cn.fandmc.flametech.gui.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.PaginatedGUI;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.gui.components.NavigationComponent;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * 矿物/材料GUI
 */
public class MaterialsGUI extends PaginatedGUI {

    private boolean materialsLoaded = false; // 标记材料是否已加载

    public MaterialsGUI(Main plugin) {
        super(plugin, "materials", plugin.getConfigManager().getLang("gui.materials.title"));
        setParentGUI("main");
    }

    @Override
    protected void buildGUI(Player player) {
        try {
            MessageUtils.logDebug("MaterialsGUI: 开始构建GUI for player " + player.getName() +
                    ", materialsLoaded=" + materialsLoaded +
                    ", currentPage=" + getCurrentPage());

            // 只在第一次打开或强制刷新时加载材料
            if (!materialsLoaded || pageItems.isEmpty()) {
                MessageUtils.logDebug("MaterialsGUI: 重新加载材料数据");
                loadMaterialsWithErrorHandling();
                materialsLoaded = true;
            } else {
                MessageUtils.logDebug("MaterialsGUI: 使用已缓存的材料数据，项目数=" + pageItems.size());
            }

            // 构建GUI
            super.buildGUI(player);

            // 记录构建结果
            MessageUtils.logDebug("MaterialsGUI: GUI构建完成 - 当前页=" + getCurrentPage() +
                    ", 总页数=" + getTotalPages() +
                    ", 材料数=" + pageItems.size());

        } catch (Exception e) {
            MessageUtils.logError("MaterialsGUI构建失败: " + e.getMessage());
            e.printStackTrace();

            // 出错时重置并显示错误
            resetAndShowError("GUI构建失败: " + e.getMessage());
            super.buildGUI(player);
        }
    }

    /**
     * 强制重新加载材料（用于刷新）
     */
    public void forceReload() {
        MessageUtils.logDebug("MaterialsGUI: 强制重新加载材料");
        materialsLoaded = false;
        clearPageItems();
        currentPage = 0;
    }

    /**
     * 带错误处理的材料加载
     */
    private void loadMaterialsWithErrorHandling() {
        try {
            // 清空现有数据
            clearPageItems();

            Collection<Material> allMaterials = plugin.getMaterialManager().getAllMaterials();
            MessageUtils.logDebug("MaterialsGUI: 从管理器获取到 " + allMaterials.size() + " 个材料");

            if (allMaterials.isEmpty()) {
                MessageUtils.logDebug("MaterialsGUI: 没有注册的材料，显示空提示");
                addPageItem(new EmptyMaterialsComponent());
                return;
            }

            // 转换为列表并保持注册顺序（MaterialManager使用LinkedHashMap保持顺序）
            List<Material> materialsList = new ArrayList<>(allMaterials);

            MessageUtils.logDebug("MaterialsGUI: 保持注册顺序的材料 (前5个): " +
                    materialsList.stream()
                            .limit(5)
                            .map(Material::getMaterialId)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("none"));

            // 添加材料组件（保持注册顺序）
            int successCount = 0;
            int errorCount = 0;

            for (Material material : materialsList) {
                try {
                    MaterialDisplayComponent component = new MaterialDisplayComponent(material);
                    addPageItem(component);
                    successCount++;
                } catch (Exception e) {
                    MessageUtils.logError("创建材料组件失败 (" + material.getMaterialId() + "): " + e.getMessage());
                    errorCount++;
                }
            }

            MessageUtils.logDebug("MaterialsGUI: 材料加载完成 - 成功:" + successCount + ", 失败:" + errorCount);

            // 如果所有材料都失败了，显示错误
            if (successCount == 0 && errorCount > 0) {
                addPageItem(new ErrorComponent("所有材料加载失败"));
            }

        } catch (Exception e) {
            MessageUtils.logError("加载材料列表时发生错误: " + e.getMessage());
            resetAndShowError("材料系统错误: " + e.getMessage());
        }
    }

    /**
     * 重置并显示错误
     */
    private void resetAndShowError(String errorMessage) {
        clearPageItems();
        addPageItem(new ErrorComponent(errorMessage));
        materialsLoaded = true; // 标记为已加载，避免重复尝试
        currentPage = 0;
    }

    @Override
    protected void buildExtraComponents(Player player) {
        try {
            // 返回主界面按钮
            setComponent(45, new NavigationComponent(NavigationComponent.NavigationType.BACK, getParentGUIId()));

            // 添加刷新按钮
            setComponent(46, new RefreshComponent());

            // 添加调试信息按钮（仅在调试模式下）
            if (plugin.isDebugMode()) {
                setComponent(53, new DebugInfoComponent());
            }

        } catch (Exception e) {
            MessageUtils.logError("构建额外组件时发生错误: " + e.getMessage());
        }
    }

    /**
     * 刷新组件
     */
    private class RefreshComponent implements GUIComponent {
        @Override
        public ItemStack getDisplayItem() {
            return new cn.fandmc.flametech.items.builders.ItemBuilder(org.bukkit.Material.EMERALD)
                    .displayName("§a刷新材料列表")
                    .lore(
                            "§7重新加载所有材料",
                            "§7用于更新材料显示",
                            "",
                            "§e点击刷新"
                    )
                    .build();
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            try {
                MessageUtils.sendMessage(player, "§a正在刷新材料列表...");
                forceReload();

                // 重新打开GUI
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    try {
                        plugin.getGuiManager().openGUI(player, "materials");
                        MessageUtils.sendMessage(player, "§a材料列表已刷新！");
                    } catch (Exception e) {
                        MessageUtils.logError("刷新后重新打开GUI失败: " + e.getMessage());
                        MessageUtils.sendMessage(player, "§c刷新失败: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                MessageUtils.logError("刷新材料时发生错误: " + e.getMessage());
                MessageUtils.sendMessage(player, "§c刷新失败: " + e.getMessage());
            }
        }

        @Override
        public boolean isClickable(Player player) {
            return true;
        }
    }

    /**
     * 材料显示组件 - 增强版本
     */
    private class MaterialDisplayComponent implements GUIComponent {
        private final Material material;
        private final String materialId;
        private final String displayName;

        public MaterialDisplayComponent(Material material) {
            this.material = material;
            this.materialId = material.getMaterialId();
            this.displayName = material.getDisplayName();
        }

        @Override
        public ItemStack getDisplayItem() {
            try {
                ItemStack materialItem = material.createItem();

                if (materialItem == null) {
                    MessageUtils.logWarning("材料 " + materialId + " 创建的物品为null");
                    return createErrorItem("创建失败");
                }

                // 处理物品元数据
                if (materialItem.hasItemMeta()) {
                    ItemMeta meta = materialItem.getItemMeta();
                    List<String> lore = meta.getLore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }

                    // 添加查看器提示
                    lore.add("");
                    lore.add("§7左键：查看详细信息");
                    lore.add("§7右键：获取材料");
                    lore.add("§7Shift+左键：查看相关配方");

                    // 调试模式下显示ID
                    if (plugin.isDebugMode()) {
                        lore.add("§8[Debug] ID: " + materialId);
                    }

                    // 处理颜色代码
                    List<String> processedLore = new ArrayList<>();
                    for (String line : lore) {
                        processedLore.add(MessageUtils.colorize(line));
                    }

                    meta.setLore(processedLore);
                    materialItem.setItemMeta(meta);
                }

                return materialItem;

            } catch (Exception e) {
                MessageUtils.logError("创建材料 " + materialId + " 的显示物品时发生错误: " + e.getMessage());
                return createErrorItem("显示错误");
            }
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            try {
                if (event.isLeftClick() && !event.isShiftClick()) {
                    // 左键：打开材料查看器
                    openMaterialViewer(player);
                } else if (event.isRightClick() && !event.isShiftClick()) {
                    // 右键：给予材料
                    giveMaterialToPlayer(player, 1);
                } else if (event.isLeftClick() && event.isShiftClick()) {
                    // Shift+左键：直接查看相关配方
                    openRelatedRecipes(player);
                } else if (event.isRightClick() && event.isShiftClick()) {
                    // Shift+右键：给予一组材料
                    giveMaterialToPlayer(player, 16);
                }

            } catch (Exception e) {
                MessageUtils.logError("处理材料点击时发生错误: " + e.getMessage());
                MessageUtils.sendMessage(player, "§c操作失败: " + e.getMessage());
            }
        }

        @Override
        public boolean isVisible(Player player) {
            return true;
        }

        @Override
        public boolean isClickable(Player player) {
            return true;
        }

        /**
         * 创建错误显示物品
         */
        private ItemStack createErrorItem(String error) {
            return new cn.fandmc.flametech.items.builders.ItemBuilder(org.bukkit.Material.BARRIER)
                    .displayName("§c" + displayName + " (错误)")
                    .lore("§7" + error, "§8ID: " + materialId)
                    .build();
        }

        /**
         * 打开材料查看器
         */
        private void openMaterialViewer(Player player) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    MaterialViewerGUI materialViewer = new MaterialViewerGUI(plugin, material, "materials");
                    materialViewer.open(player);
                    MessageUtils.sendMessage(player, "§a正在打开材料查看器...");
                } catch (Exception e) {
                    MessageUtils.logError("打开材料查看器失败: " + e.getMessage());
                    MessageUtils.sendMessage(player, "§c打开材料查看器时发生错误");
                }
            });
        }

        /**
         * 直接打开相关配方列表
         */
        private void openRelatedRecipes(Player player) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    // 查找使用此材料的配方
                    List<Recipe> relatedRecipes = findRecipesUsingMaterial(material);

                    if (relatedRecipes.isEmpty()) {
                        MessageUtils.sendMessage(player, "§e该材料暂无相关配方");
                        return;
                    }

                    MaterialRelatedRecipesGUI recipesGUI = new MaterialRelatedRecipesGUI(
                            plugin, material, relatedRecipes, "materials");
                    recipesGUI.open(player);
                    MessageUtils.sendMessage(player, "§a找到 " + relatedRecipes.size() + " 个相关配方");
                } catch (Exception e) {
                    MessageUtils.logError("打开相关配方列表失败: " + e.getMessage());
                    MessageUtils.sendMessage(player, "§c打开相关配方时发生错误");
                }
            });
        }

        /**
         * 给予玩家材料
         */
        private void giveMaterialToPlayer(Player player, int amount) {
            try {
                ItemStack materialItem = material.createItem(amount);
                if (materialItem != null) {
                    Map<Integer, ItemStack> leftover = player.getInventory().addItem(materialItem);

                    if (leftover.isEmpty()) {
                        MessageUtils.sendMessage(player, "§a获得了 " + amount + " 个 " + displayName);
                    } else {
                        // 部分物品掉落
                        for (ItemStack overflow : leftover.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), overflow);
                        }
                        int received = amount - leftover.values().stream().mapToInt(ItemStack::getAmount).sum();
                        MessageUtils.sendMessage(player, "§e获得了 " + received + " 个 " + displayName + "，剩余掉落在地上");
                    }
                } else {
                    MessageUtils.sendMessage(player, "§c无法创建材料物品");
                }
            } catch (Exception e) {
                MessageUtils.logError("给予材料失败: " + e.getMessage());
                MessageUtils.sendMessage(player, "§c给予材料时发生错误");
            }
        }

        /**
         * 查找使用指定材料的配方
         */
        private List<Recipe> findRecipesUsingMaterial(Material targetMaterial) {
            List<Recipe> usedInRecipes = new ArrayList<>();
            ItemStack materialItem = targetMaterial.createItem();

            for (Recipe recipe : plugin.getRecipeManager().getAllRecipes()) {
                Map<Integer, ItemStack> ingredients = recipe.getIngredients();
                for (ItemStack ingredient : ingredients.values()) {
                    if (ingredient != null && isSameMaterial(ingredient, materialItem)) {
                        usedInRecipes.add(recipe);
                        break; // 避免重复添加同一配方
                    }
                }
            }

            return usedInRecipes;
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
    }
    /**
     * 空材料提示组件
     */
    private class EmptyMaterialsComponent implements GUIComponent {
        @Override
        public ItemStack getDisplayItem() {
            return new cn.fandmc.flametech.items.builders.ItemBuilder(org.bukkit.Material.STRUCTURE_VOID)
                    .displayName("§c暂无材料")
                    .lore(
                            "§7还没有注册任何材料",
                            "§7可能原因：",
                            "§7• 材料系统未初始化",
                            "§7• 所有材料注册失败",
                            "",
                            "§7尝试点击刷新按钮重新加载",
                            "§7敬请期待后续更新！"
                    )
                    .build();
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            MessageUtils.sendMessage(player, "§7当前还没有可用的材料，尝试点击刷新按钮");
        }

        @Override
        public boolean isClickable(Player player) {
            return false;
        }
    }

    /**
     * 错误提示组件
     */
    private class ErrorComponent implements GUIComponent {
        private final String errorMessage;

        public ErrorComponent(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public ItemStack getDisplayItem() {
            return new cn.fandmc.flametech.items.builders.ItemBuilder(org.bukkit.Material.BARRIER)
                    .displayName("§c系统错误")
                    .lore(
                            "§7材料系统发生错误:",
                            "§c" + errorMessage,
                            "",
                            "§7建议操作:",
                            "§7• 点击刷新按钮重试",
                            "§7• 使用 /flametech reload 重载插件",
                            "§7• 检查控制台错误日志",
                            "§7• 联系管理员检查配置"
                    )
                    .build();
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            MessageUtils.sendMessage(player, "§c系统错误: " + errorMessage);
            MessageUtils.sendMessage(player, "§7请尝试点击刷新按钮或重载插件");
        }

        @Override
        public boolean isClickable(Player player) {
            return false;
        }
    }

    /**
     * 调试信息组件
     */
    private class DebugInfoComponent implements GUIComponent {
        @Override
        public ItemStack getDisplayItem() {
            return new cn.fandmc.flametech.items.builders.ItemBuilder(org.bukkit.Material.REDSTONE_TORCH)
                    .displayName("§c[Debug] 分页信息")
                    .lore(
                            "§7当前页: §e" + (getCurrentPage() + 1),
                            "§7总页数: §e" + getTotalPages(),
                            "§7项目数: §e" + pageItems.size(),
                            "§7每页显示: §e" + contentSlots.length,
                            "§7已加载: §e" + materialsLoaded,
                            "",
                            "§e点击输出详细调试信息"
                    )
                    .build();
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            MessageUtils.sendMessage(player, "§c=== MaterialsGUI 调试信息 ===");
            MessageUtils.sendMessage(player, "§7当前页: §e" + (getCurrentPage() + 1) + "§7/§e" + getTotalPages());
            MessageUtils.sendMessage(player, "§7页面项目总数: §e" + pageItems.size());
            MessageUtils.sendMessage(player, "§7每页可显示: §e" + contentSlots.length);
            MessageUtils.sendMessage(player, "§7材料已加载: §e" + materialsLoaded);

            int currentPage = getCurrentPage();
            int itemsPerPage = contentSlots.length;
            int startIndex = currentPage * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, pageItems.size());

            MessageUtils.sendMessage(player, "§7当前页显示范围: §e" + startIndex + " - " + (endIndex - 1));

            boolean hasNext = currentPage < (getTotalPages() - 1);
            boolean hasPrev = currentPage > 0;

            MessageUtils.sendMessage(player, "§7可以下一页: §e" + hasNext);
            MessageUtils.sendMessage(player, "§7可以上一页: §e" + hasPrev);
        }

        @Override
        public boolean isClickable(Player player) {
            return true;
        }
    }
}