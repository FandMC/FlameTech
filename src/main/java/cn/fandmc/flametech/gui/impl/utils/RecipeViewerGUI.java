package cn.fandmc.flametech.gui.impl.utils;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
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
import java.util.List;
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
        if (displayName.contains(plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_LANGUAGE_KEY_NOT_FOUND))) {
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
                                MessageUtils.sendMessage(player, plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_STRUCTURE_NOT_FOUND));
                            }
                        } catch (Exception e) {
                            MessageUtils.logError("Failed to open structure layout GUI: " + e.getMessage());
                            MessageUtils.sendMessage(player, plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_STRUCTURE_LAYOUT_ERROR));
                        }
                    });
                } catch (Exception e) {
                    MessageUtils.logError("Error in structure info button: " + e.getMessage());
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_OPERATION_FAILED));
                }
            }
        });

        // 查看配方按钮
        setComponent(VIEW_RECIPES_SLOT, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                return new ItemBuilder(Material.CRAFTING_TABLE)
                        .displayName(plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_VIEW_RECIPES_NAME))
                        .lore(plugin.getConfigManager().getStringList(Messages.GUI_RECIPE_VIEWER_VIEW_RECIPES_LORE))
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
                            MessageUtils.sendMessage(player, plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_RECIPES_LIST_ERROR));
                        }
                    });
                } catch (Exception e) {
                    MessageUtils.logError("Error in view recipes button: " + e.getMessage());
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_OPERATION_FAILED));
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
            List<String> failedLore = plugin.getConfigManager().getStringList(Messages.GUI_RECIPE_VIEWER_STRUCTURE_LOAD_FAILED_LORE);
            // 替换参数
            for (int i = 0; i < failedLore.size(); i++) {
                failedLore.set(i, failedLore.get(i).replace("%id%", multiblockId));
            }

            return new ItemBuilder(Material.BARRIER)
                    .displayName(plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_STRUCTURE_LOAD_FAILED))
                    .lore(failedLore)
                    .build();
        }

        MultiblockStructure structure = structureOpt.get();
        String displayName = getMultiblockDisplayName(plugin, multiblockId);

        // 获取结构信息lore并替换参数
        List<String> structureLore = plugin.getConfigManager().getStringList(Messages.GUI_RECIPE_VIEWER_STRUCTURE_INFO_LORE);
        for (int i = 0; i < structureLore.size(); i++) {
            String line = structureLore.get(i);
            line = line.replace("%name%", displayName);
            line = line.replace("%blocks%", String.valueOf(structure.getBlockCount()));
            line = line.replace("%dimensions%",
                    structure.getBoundingBox().getWidth() + "x" +
                            structure.getBoundingBox().getHeight() + "x" +
                            structure.getBoundingBox().getDepth());
            structureLore.set(i, line);
        }

        return new ItemBuilder(Material.STRUCTURE_BLOCK)
                .displayName(plugin.getConfigManager().getLang(Messages.GUI_RECIPE_VIEWER_STRUCTURE_INFO_NAME))
                .lore(structureLore)
                .glow()
                .build();
    }
}