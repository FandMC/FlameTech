package cn.fandmc.flametech.gui.impl.utils;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.BaseGUI;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.gui.components.StaticComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.recipes.base.Recipe;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 材料查看器GUI - 显示材料信息和相关配方
 */
public class MaterialViewerGUI extends BaseGUI {

    private final Material material;
    private final String fromGUI;

    // 界面布局槽位
    private static final int MATERIAL_INFO_SLOT = 11;
    private static final int MATERIAL_DISPLAY_SLOT = 13;
    private static final int RELATED_RECIPES_SLOT = 15;
    private static final int BACK_SLOT = 45;

    public MaterialViewerGUI(Main plugin, Material material, String fromGUI) {
        super(plugin, "material_viewer_" + material.getMaterialId(), 54,
                plugin.getConfigManager().getLang("gui.material_viewer.title")
                        .replace("%material%", material.getDisplayName()));
        this.material = material;
        this.fromGUI = fromGUI != null ? fromGUI : "materials";
        setParentGUI(fromGUI);
    }

    @Override
    protected void buildGUI(Player player) {
        clearComponents();

        // 设置边框
        setupBorder();

        // 材料信息面板
        setComponent(MATERIAL_INFO_SLOT, new StaticComponent(createMaterialInfoPanel()));

        // 材料展示
        setComponent(MATERIAL_DISPLAY_SLOT, new StaticComponent(createMaterialDisplay()));

        // 相关配方按钮
        setComponent(RELATED_RECIPES_SLOT, new RelatedRecipesComponent());

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
                        if (getParentGUIId() != null) {
                            plugin.getGuiManager().openGUI(player, getParentGUIId());
                        } else {
                            player.closeInventory();
                        }
                    });
                } catch (Exception e) {
                    MessageUtils.logError("Error in back button: " + e.getMessage());
                    player.closeInventory();
                }
            }
        });
    }

    /**
     * 设置边框
     */
    private void setupBorder() {
        ItemStack borderItem = ItemBuilder.createBorderItem();
        StaticComponent borderComponent = new StaticComponent(borderItem);

        // 顶部和底部边框
        for (int i = 0; i < 9; i++) {
            setComponent(i, borderComponent);
            setComponent(45 + i, borderComponent);
        }

        // 侧边边框
        setComponent(9, borderComponent);
        setComponent(17, borderComponent);
        setComponent(18, borderComponent);
        setComponent(26, borderComponent);
        setComponent(27, borderComponent);
        setComponent(35, borderComponent);
        setComponent(36, borderComponent);
        setComponent(44, borderComponent);
    }

    /**
     * 创建材料信息面板
     */
    private ItemStack createMaterialInfoPanel() {
        List<String> lore = new ArrayList<>();

        // 基础信息
        lore.add(plugin.getConfigManager().getLang("gui.material_viewer.info.category") + getFormattedCategory());
        lore.add(plugin.getConfigManager().getLang("gui.material_viewer.info.id") + material.getMaterialId());
        lore.add("");

        // 获取制作方式
        List<Recipe> sourceRecipes = findSourceRecipes();
        if (!sourceRecipes.isEmpty()) {
            lore.add(plugin.getConfigManager().getLang("gui.material_viewer.info.created_by"));
            for (Recipe recipe : sourceRecipes) {
                lore.add("  §7• §e" + recipe.getDisplayName());
            }
        } else {
            lore.add(plugin.getConfigManager().getLang("gui.material_viewer.info.no_source_recipes"));
        }

        lore.add("");

        // 用途统计
        List<Recipe> usedInRecipes = findRecipesUsingMaterial();
        lore.add(plugin.getConfigManager().getLang("gui.material_viewer.info.used_in_count",
                "%count%", String.valueOf(usedInRecipes.size())));

        lore.add("");
        lore.add(plugin.getConfigManager().getLang("gui.material_viewer.info.view_recipes_hint"));

        return new ItemBuilder(org.bukkit.Material.BOOK)
                .displayName(plugin.getConfigManager().getLang("gui.material_viewer.info.title"))
                .lore(lore)
                .build();
    }

    /**
     * 创建材料展示物品
     */
    private ItemStack createMaterialDisplay() {
        ItemStack materialItem = material.createItem();

        if (materialItem.hasItemMeta()) {
            var meta = materialItem.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();

            // 添加查看器特有的信息
            lore.add("");
            lore.add(plugin.getConfigManager().getLang("gui.material_viewer.display.material_info"));
            lore.add(plugin.getConfigManager().getLang("gui.material_viewer.display.category",
                    "%category%", getFormattedCategory()));

            meta.setLore(lore);
            materialItem.setItemMeta(meta);
        }

        return materialItem;
    }

    /**
     * 获取格式化的材料类别
     */
    private String getFormattedCategory() {
        String materialId = material.getMaterialId();
        String category = determineMaterialCategory(materialId);

        return switch (category) {
            case "dusts" -> "§7粉末";
            case "ingots" -> "§f锭类";
            case "alloys" -> "§6合金";
            case "gems" -> "§b宝石";
            case "processed" -> "§e加工品";
            case "raw_materials" -> "§8原材料";
            default -> "§7其他";
        };
    }

    /**
     * 确定材料类别
     */
    private String determineMaterialCategory(String materialId) {
        if (materialId.contains("dust")) return "dusts";
        if (materialId.contains("ingot")) return "ingots";
        if (materialId.contains("artificial")) return "gems";
        if (materialId.equals("ore_dust")) return "raw_materials";
        if (materialId.contains("silicon")) return "processed";
        if (materialId.contains("bronze")) return "alloys";
        return "other";
    }

    /**
     * 查找制作此材料的配方
     */
    private List<Recipe> findSourceRecipes() {
        List<Recipe> sourceRecipes = new ArrayList<>();
        ItemStack materialItem = material.createItem();

        for (Recipe recipe : plugin.getRecipeManager().getAllRecipes()) {
            ItemStack result = recipe.getResult();
            if (result != null && isSameMaterial(result, materialItem)) {
                sourceRecipes.add(recipe);
            }
        }

        return sourceRecipes;
    }

    /**
     * 查找使用此材料的配方
     */
    private List<Recipe> findRecipesUsingMaterial() {
        List<Recipe> usedInRecipes = new ArrayList<>();
        ItemStack materialItem = material.createItem();

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
     * 检查两个物品是否为同一材料（忽略数量）
     */
    private boolean isSameMaterial(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;

        // 基础类型检查
        if (item1.getType() != item2.getType()) return false;

        // 检查是否都是FlameTech材料
        boolean isItem1FTMaterial = plugin.getMaterialManager().isMaterial(item1);
        boolean isItem2FTMaterial = plugin.getMaterialManager().isMaterial(item2);

        if (isItem1FTMaterial && isItem2FTMaterial) {
            // 都是FT材料，通过材料ID比较
            var material1 = plugin.getMaterialManager().getMaterialFromStack(item1);
            var material2 = plugin.getMaterialManager().getMaterialFromStack(item2);

            if (material1.isPresent() && material2.isPresent()) {
                return material1.get().getMaterialId().equals(material2.get().getMaterialId());
            }
        } else if (!isItem1FTMaterial && !isItem2FTMaterial) {
            // 都是原版材料，通过类型和NBT比较
            return item1.isSimilar(item2);
        }

        return false;
    }

    /**
     * 相关配方组件
     */
    private class RelatedRecipesComponent implements GUIComponent {
        @Override
        public ItemStack getDisplayItem() {
            List<Recipe> relatedRecipes = findRecipesUsingMaterial();

            List<String> lore = new ArrayList<>();
            lore.add(plugin.getConfigManager().getLang("gui.material_viewer.related_recipes.description"));
            lore.add("");

            if (relatedRecipes.isEmpty()) {
                lore.add(plugin.getConfigManager().getLang("gui.material_viewer.related_recipes.no_recipes"));
            } else {
                lore.add(plugin.getConfigManager().getLang("gui.material_viewer.related_recipes.count",
                        "%count%", String.valueOf(relatedRecipes.size())));
                lore.add("");

                // 显示前几个配方名称
                int maxDisplay = Math.min(5, relatedRecipes.size());
                for (int i = 0; i < maxDisplay; i++) {
                    lore.add("§7• §e" + relatedRecipes.get(i).getDisplayName());
                }

                if (relatedRecipes.size() > maxDisplay) {
                    lore.add("§7• §6... 还有 " + (relatedRecipes.size() - maxDisplay) + " 个配方");
                }
            }

            lore.add("");
            if (!relatedRecipes.isEmpty()) {
                lore.add(plugin.getConfigManager().getLang("gui.material_viewer.related_recipes.click_to_view"));
            }

            org.bukkit.Material iconMaterial = relatedRecipes.isEmpty() ?
                    org.bukkit.Material.BARRIER : org.bukkit.Material.CRAFTING_TABLE;

            return new ItemBuilder(iconMaterial)
                    .displayName(plugin.getConfigManager().getLang("gui.material_viewer.related_recipes.title"))
                    .lore(lore)
                    .glow(!relatedRecipes.isEmpty())
                    .build();
        }

        @Override
        public void onClick(Player player, InventoryClickEvent event) {
            List<Recipe> relatedRecipes = findRecipesUsingMaterial();

            if (relatedRecipes.isEmpty()) {
                MessageUtils.sendMessage(player,
                        plugin.getConfigManager().getLang("gui.material_viewer.related_recipes.no_recipes_message"));
                return;
            }

            try {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    try {
                        MaterialRelatedRecipesGUI recipesGUI = new MaterialRelatedRecipesGUI(
                                plugin, material, relatedRecipes, getGuiId());
                        recipesGUI.open(player);
                    } catch (Exception e) {
                        MessageUtils.logError("Failed to open material related recipes GUI: " + e.getMessage());
                        MessageUtils.sendMessage(player,
                                plugin.getConfigManager().getLang("gui.material_viewer.related_recipes.open_error"));
                    }
                });
            } catch (Exception e) {
                MessageUtils.logError("Error in related recipes button: " + e.getMessage());
                MessageUtils.sendMessage(player,
                        plugin.getConfigManager().getLang("gui.material_viewer.related_recipes.open_error"));
            }
        }

        @Override
        public boolean isClickable(Player player) {
            return !findRecipesUsingMaterial().isEmpty();
        }
    }
}