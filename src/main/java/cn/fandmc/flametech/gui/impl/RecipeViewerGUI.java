package cn.fandmc.flametech.gui.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.BaseGUI;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.gui.components.StaticComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.multiblock.base.MultiblockStructure;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 配方查看器GUI
 */
public class RecipeViewerGUI extends BaseGUI {

    private final String multiblockId;
    private static final Map<String, RecipeViewerGUI> instances = new HashMap<>();

    private static final int STRUCTURE_INFO_SLOT = 10;
    private static final int VIEW_RECIPES_SLOT = 16;
    private static final int BACK_SLOT = 22;

    private RecipeViewerGUI(Main plugin, String multiblockId) {
        super(plugin, "recipe_viewer_" + multiblockId, 27,
                plugin.getConfigManager().getLang("gui.recipe_viewer.title")
                        .replace("%machine%", getMultiblockDisplayName(plugin, multiblockId)));
        this.multiblockId = multiblockId;
        setParentGUI("basic_machines");
    }

    private static String getMultiblockDisplayName(Main plugin, String multiblockId) {
        // 尝试从配置获取显示名称
        String langKey = "multiblock." + multiblockId + ".name";
        String displayName = plugin.getConfigManager().getLang(langKey);

        // 如果没有找到配置，尝试从结构获取
        if (displayName.contains("未找到语言键")) {
            Optional<MultiblockStructure> structureOpt = plugin.getMultiblockManager().getStructure(multiblockId);
            if (structureOpt.isPresent()) {
                return structureOpt.get().getDisplayName();
            }
        }

        return displayName;
    }

    public static RecipeViewerGUI getInstance(Main plugin, String multiblockId) {
        return instances.computeIfAbsent(multiblockId, k -> {
            RecipeViewerGUI gui = new RecipeViewerGUI(plugin, multiblockId);
            plugin.getGuiManager().registerGUI(gui);
            return gui;
        });
    }

    @Override
    protected void buildGUI(Player player) {
        clearComponents();

        // 设置边框
        setupBorder();

        // 结构信息（可点击）
        setComponent(STRUCTURE_INFO_SLOT, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                return createStructureInfo();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                try {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        try {
                            Optional<MultiblockStructure> structureOpt = plugin.getMultiblockManager().getStructure(multiblockId);
                            if (structureOpt.isPresent()) {
                                StructureLayoutGUI layoutGUI = new StructureLayoutGUI(plugin, structureOpt.get(), multiblockId);
                                layoutGUI.open(player);
                            } else {
                                MessageUtils.sendMessage(player, "&c无法找到结构定义");
                            }
                        } catch (Exception e) {
                            MessageUtils.logError("Failed to open structure layout GUI: " + e.getMessage());
                            MessageUtils.sendMessage(player, "&c打开结构布局时发生错误");
                        }
                    });
                } catch (Exception e) {
                    MessageUtils.logError("Error in structure info button: " + e.getMessage());
                    MessageUtils.sendMessage(player, "&c操作失败，请稍后重试");
                }
            }
        });

        // 查看配方按钮
        setComponent(VIEW_RECIPES_SLOT, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                return new ItemBuilder(Material.CRAFTING_TABLE)
                        .displayName("&a查看配方")
                        .lore(
                                "&7查看此结构的所有配方",
                                "&7包含合成材料和产出物",
                                "",
                                "&e点击打开配方列表"
                        )
                        .build();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                try {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        try {
                            StructureRecipesGUI recipesGUI = StructureRecipesGUI.getInstance(plugin, multiblockId);
                            recipesGUI.setParentGUI("recipe_viewer_" + multiblockId);
                            recipesGUI.open(player);
                        } catch (Exception e) {
                            MessageUtils.logError("Failed to open structure recipes GUI: " + e.getMessage());
                            MessageUtils.sendMessage(player, "&c打开配方列表时发生错误");
                        }
                    });
                } catch (Exception e) {
                    MessageUtils.logError("Error in view recipes button: " + e.getMessage());
                    MessageUtils.sendMessage(player, "&c操作失败，请稍后重试");
                }
            }
        });

        // 返回按钮
        if (getParentGUIId() != null) {
            setComponent(BACK_SLOT, new GUIComponent() {
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

    private void setupBorder() {
        ItemStack borderItem = ItemBuilder.createBorderItem();
        StaticComponent borderComponent = new StaticComponent(borderItem);

        // 顶部和底部边框
        for (int i = 0; i < 9; i++) {
            setComponent(i, borderComponent);
            setComponent(18 + i, borderComponent);
        }

        // 侧边边框
        setComponent(9, borderComponent);
        setComponent(17, borderComponent);
    }

    private ItemStack createStructureInfo() {
        Optional<MultiblockStructure> structureOpt = plugin.getMultiblockManager().getStructure(multiblockId);

        if (structureOpt.isEmpty()) {
            return new ItemBuilder(Material.BARRIER)
                    .displayName("&c结构加载失败")
                    .lore(
                            "&7无法找到结构定义",
                            "&7结构ID: &e" + multiblockId,
                            "",
                            "&c请联系管理员检查插件配置"
                    )
                    .build();
        }

        MultiblockStructure structure = structureOpt.get();
        String displayName = getMultiblockDisplayName(plugin, multiblockId);

        return new ItemBuilder(Material.STRUCTURE_BLOCK)
                .displayName("&6&l结构布局图")
                .lore(
                        "&7结构名称: &e" + displayName,
                        "&7方块数量: &e" + structure.getBlockCount(),
                        "&7结构尺寸: &e" +
                                structure.getBoundingBox().getWidth() + "x" +
                                structure.getBoundingBox().getHeight() + "x" +
                                structure.getBoundingBox().getDepth(),
                        "",
                        "&7搭建说明:",
                        "&7• 按照指定布局搭建",
                        "&7• 搭建完成后右键主方块",
                        "&7• 严格按照层级和位置摆放",
                        "",
                        "&a&l▶ 点击查看详细搭建图",
                        "&7查看每一层的具体摆放方式",
                        "",
                        "&e[结构布局]"
                )
                .glow()
                .build();
    }
}