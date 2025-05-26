package cn.fandmc.flametech.gui.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.BaseGUI;
import cn.fandmc.flametech.gui.components.GUIComponent;
import cn.fandmc.flametech.gui.components.StaticComponent;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import cn.fandmc.flametech.multiblock.base.BlockOffset;
import cn.fandmc.flametech.multiblock.base.MultiblockStructure;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 结构布局展示GUI
 */
public class StructureLayoutGUI extends BaseGUI {

    private final MultiblockStructure structure;
    private final String multiblockId;
    private int currentLayer = 0;
    private final Map<Integer, Map<BlockOffset, Material>> layerStructure;
    private final int minY, maxY;

    private static final int[] DISPLAY_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private static final int LAYER_UP_SLOT = 8;
    private static final int LAYER_INFO_SLOT = 17;
    private static final int LAYER_DOWN_SLOT = 26;
    private static final int BACK_SLOT = 45;
    private static final int INFO_SLOT = 53;

    public StructureLayoutGUI(Main plugin, MultiblockStructure structure, String multiblockId) {
        super(plugin, "structure_layout_" + multiblockId, 54,
                "&6结构布局: " + structure.getDisplayName());
        this.structure = structure;
        this.multiblockId = multiblockId;

        // 按层级组织结构
        this.layerStructure = organizeByLayers();

        // 计算Y轴范围
        MultiblockStructure.BoundingBox bbox = structure.getBoundingBox();
        this.minY = bbox.getMinY();
        this.maxY = bbox.getMaxY();

        // 从最底层开始显示
        this.currentLayer = minY;

        setParentGUI("recipe_viewer_" + multiblockId);
    }

    private Map<Integer, Map<BlockOffset, Material>> organizeByLayers() {
        Map<Integer, Map<BlockOffset, Material>> layers = new TreeMap<>();
        Map<BlockOffset, Material> structureBlocks = structure.getStructure();

        if (structureBlocks.isEmpty()) {
            // 如果结构为空，创建一个默认层
            Map<BlockOffset, Material> defaultLayer = new HashMap<>();
            defaultLayer.put(new BlockOffset(0, 0, 0), Material.BEDROCK);
            layers.put(0, defaultLayer);
            return layers;
        }

        for (Map.Entry<BlockOffset, Material> entry : structureBlocks.entrySet()) {
            BlockOffset offset = entry.getKey();
            Material material = entry.getValue();
            int y = offset.getY();

            layers.computeIfAbsent(y, k -> new HashMap<>()).put(offset, material);
        }

        return layers;
    }

    @Override
    protected void buildGUI(Player player) {
        clearComponents();

        // 设置边框
        setupBorder();

        // 显示当前层级
        displayCurrentLayer();

        // 设置控制按钮
        setupControlButtons();

        // 显示信息
        setupInfoDisplay();
    }

    private void setupBorder() {
        ItemStack borderItem = ItemBuilder.createBorderItem();
        StaticComponent borderComponent = new StaticComponent(borderItem);

        // 顶部和底部边框
        for (int i = 0; i < 9; i++) {
            setComponent(i, borderComponent);
            setComponent(45 + i, borderComponent);
        }

        // 侧边边框（避开显示区域）
        setComponent(9, borderComponent);
        setComponent(18, borderComponent);
        setComponent(27, borderComponent);
        setComponent(36, borderComponent);
    }

    private void displayCurrentLayer() {
        // 清空显示区域
        for (int slot : DISPLAY_SLOTS) {
            removeComponent(slot);
        }

        Map<BlockOffset, Material> currentLayerBlocks = layerStructure.get(currentLayer);
        if (currentLayerBlocks == null || currentLayerBlocks.isEmpty()) {
            // 显示空层级提示
            setComponent(22, new StaticComponent(
                    new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS)
                            .displayName("&7空层级")
                            .lore(
                                    "&7此层级没有方块",
                                    "&7Y = " + currentLayer,
                                    "",
                                    "&7使用上下按钮切换到有方块的层级"
                            )
                            .build()
            ));
            return;
        }

        // 计算显示区域的边界（7x3网格）
        int centerX = 3; // 显示区域中心X
        int centerZ = 1; // 显示区域中心Z

        // 找到当前层的方块范围
        int minX = currentLayerBlocks.keySet().stream().mapToInt(BlockOffset::getX).min().orElse(0);
        int maxX = currentLayerBlocks.keySet().stream().mapToInt(BlockOffset::getX).max().orElse(0);
        int minZ = currentLayerBlocks.keySet().stream().mapToInt(BlockOffset::getZ).min().orElse(0);
        int maxZ = currentLayerBlocks.keySet().stream().mapToInt(BlockOffset::getZ).max().orElse(0);

        // 如果结构太大，显示警告
        if ((maxX - minX) > 6 || (maxZ - minZ) > 2) {
            setComponent(13, new StaticComponent(
                    new ItemBuilder(Material.YELLOW_STAINED_GLASS)
                            .displayName("&e结构过大")
                            .lore(
                                    "&7此层级的结构超过了显示范围",
                                    "&7尺寸: " + (maxX - minX + 1) + "x" + (maxZ - minZ + 1),
                                    "&7显示限制: 7x3",
                                    "",
                                    "&7请参考坐标信息进行搭建"
                            )
                            .build()
            ));
        }

        // 显示方块
        for (Map.Entry<BlockOffset, Material> entry : currentLayerBlocks.entrySet()) {
            BlockOffset offset = entry.getKey();
            Material material = entry.getValue();

            // 计算在显示网格中的位置
            int displayX = offset.getX() - minX + centerX - (maxX - minX) / 2;
            int displayZ = offset.getZ() - minZ + centerZ - (maxZ - minZ) / 2;

            // 检查是否在显示范围内
            if (displayX >= 0 && displayX < 7 && displayZ >= 0 && displayZ < 3) {
                int slotIndex = displayZ * 7 + displayX;
                if (slotIndex < DISPLAY_SLOTS.length) {
                    int slot = DISPLAY_SLOTS[slotIndex];

                    ItemStack displayItem = createBlockDisplayItem(material, offset);
                    setComponent(slot, new StaticComponent(displayItem));
                }
            }
        }

        // 在空槽位显示占位符，帮助理解布局
        for (int i = 0; i < DISPLAY_SLOTS.length; i++) {
            int slot = DISPLAY_SLOTS[i];
            if (!components.containsKey(slot)) {
                // 显示透明占位符
                setComponent(slot, new StaticComponent(
                        new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                                .displayName(" ")
                                .build()
                ));
            }
        }
    }

    private ItemStack createBlockDisplayItem(Material material, BlockOffset offset) {
        String displayName;
        List<String> lore = new ArrayList<>();

        // 根据材料类型设置显示名称
        switch (material) {
            case CRAFTING_TABLE:
                displayName = "&6工作台 &7(主方块)";
                lore.add("&7这是结构的主方块");
                lore.add("&7右键点击此方块来激活结构");
                break;
            case DISPENSER:
                displayName = "&e发射器";
                lore.add("&7用于存放合成材料");
                lore.add("&7将物品放入其中进行合成");
                break;
            default:
                displayName = "&f" + formatMaterialName(material);
                lore.add("&7结构组成方块");
        }

        lore.add("");
        lore.add("&7坐标: &e(" + offset.getX() + ", " + offset.getY() + ", " + offset.getZ() + ")");

        // 如果是主方块（原点），特别标记
        if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
            lore.add("&6⭐ 主方块位置");
        }

        return new ItemBuilder(material)
                .displayName(displayName)
                .lore(lore)
                .build();
    }

    private String formatMaterialName(Material material) {
        return material.name().toLowerCase().replace('_', ' ');
    }

    private void setupControlButtons() {
        // 上一层按钮
        setComponent(LAYER_UP_SLOT, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                boolean canGoUp = currentLayer < maxY;
                return new ItemBuilder(canGoUp ? Material.LIME_STAINED_GLASS : Material.GRAY_STAINED_GLASS)
                        .displayName(canGoUp ? "&a▲ 上一层" : "&7▲ 已是最高层")
                        .lore(canGoUp ?
                                Arrays.asList("&7点击查看上一层", "&7当前: Y=" + currentLayer, "&7上层: Y=" + (currentLayer + 1)) :
                                Arrays.asList("&7已经是最高层了"))
                        .build();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                if (currentLayer < maxY) {
                    currentLayer++;
                    refresh();
                }
            }

            @Override
            public boolean isClickable(Player player) {
                return currentLayer < maxY;
            }
        });

        // 下一层按钮
        setComponent(LAYER_DOWN_SLOT, new GUIComponent() {
            @Override
            public ItemStack getDisplayItem() {
                boolean canGoDown = currentLayer > minY;
                return new ItemBuilder(canGoDown ? Material.RED_STAINED_GLASS : Material.GRAY_STAINED_GLASS)
                        .displayName(canGoDown ? "&c▼ 下一层" : "&7▼ 已是最底层")
                        .lore(canGoDown ?
                                Arrays.asList("&7点击查看下一层", "&7当前: Y=" + currentLayer, "&7下层: Y=" + (currentLayer - 1)) :
                                Arrays.asList("&7已经是最底层了"))
                        .build();
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                if (currentLayer > minY) {
                    currentLayer--;
                    refresh();
                }
            }

            @Override
            public boolean isClickable(Player player) {
                return currentLayer > minY;
            }
        });

        // 层级信息
        setComponent(LAYER_INFO_SLOT, new StaticComponent(
                new ItemBuilder(Material.PAPER)
                        .displayName("&e当前层级信息")
                        .lore(
                                "&7当前层级: &eY = " + currentLayer,
                                "&7结构范围: &eY " + minY + " ~ " + maxY,
                                "&7总层数: &e" + (maxY - minY + 1),
                                "",
                                "&7使用上下按钮切换层级"
                        )
                        .build()
        ));

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

    private void setupInfoDisplay() {
        // 结构总体信息
        setComponent(INFO_SLOT, new StaticComponent(
                new ItemBuilder(Material.BOOK)
                        .displayName("&6结构信息")
                        .lore(
                                "&7结构名称: &e" + structure.getDisplayName(),
                                "&7总方块数: &e" + structure.getBlockCount(),
                                "&7结构尺寸: &e" +
                                        (structure.getBoundingBox().getWidth()) + "x" +
                                        (structure.getBoundingBox().getHeight()) + "x" +
                                        (structure.getBoundingBox().getDepth()),
                                "",
                                "&7搭建提示:",
                                "&e• 严格按照层级从下往上搭建",
                                "&e• 确保每个方块位置正确",
                                "&e• 主方块(0,0,0)为激活点"
                        )
                        .build()
        ));
    }
}