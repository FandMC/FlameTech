package cn.fandmc.flametech.gui.impl.utils;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.constants.Messages;
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
                plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_TITLE,
                        "%name%", structure.getDisplayName()));
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
            List<String> emptyLore = new ArrayList<>(plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_EMPTY_LAYER_LORE));
            for (int i = 0; i < emptyLore.size(); i++) {
                emptyLore.set(i, emptyLore.get(i).replace("%layer%", String.valueOf(currentLayer)));
            }

            setComponent(22, new StaticComponent(
                    new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS)
                            .displayName(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_EMPTY_LAYER_NAME))
                            .lore(emptyLore)
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
            List<String> oversizedLore = new ArrayList<>(plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_OVERSIZED_LORE));
            for (int i = 0; i < oversizedLore.size(); i++) {
                String line = oversizedLore.get(i);
                line = line.replace("%size%", (maxX - minX + 1) + "x" + (maxZ - minZ + 1));
                oversizedLore.set(i, line);
            }

            setComponent(13, new StaticComponent(
                    new ItemBuilder(Material.YELLOW_STAINED_GLASS)
                            .displayName(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_OVERSIZED_NAME))
                            .lore(oversizedLore)
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

        // 处理特殊方块（不能作为物品的方块）
        Material displayMaterial = getDisplayMaterial(material);

        // 根据材料类型设置显示名称和lore
        switch (material) {
            case FIRE:
                displayName = plugin.getConfigManager().getLang("gui.structure_layout.block.fire_name");
                lore.addAll(plugin.getConfigManager().getStringList("gui.structure_layout.block.fire_lore"));
                break;
            case CRAFTING_TABLE:
                displayName = plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_BLOCK_CRAFTING_TABLE_NAME);
                lore.addAll(plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_BLOCK_CRAFTING_TABLE_LORE));
                break;
            case DISPENSER:
                displayName = plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_BLOCK_DISPENSER_NAME);
                lore.addAll(plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_BLOCK_DISPENSER_LORE));
                break;
            case NETHER_BRICK_FENCE:
                displayName = plugin.getConfigManager().getLang("gui.structure_layout.block.nether_brick_fence_name");
                lore.addAll(plugin.getConfigManager().getStringList("gui.structure_layout.block.nether_brick_fence_lore"));
                break;
            case OAK_TRAPDOOR:
                displayName = plugin.getConfigManager().getLang("gui.structure_layout.block.oak_trapdoor_name");
                lore.addAll(plugin.getConfigManager().getStringList("gui.structure_layout.block.oak_trapdoor_lore"));
                break;
            case CAULDRON:
                displayName = plugin.getConfigManager().getLang("gui.structure_layout.block.cauldron_name");
                lore.addAll(plugin.getConfigManager().getStringList("gui.structure_layout.block.cauldron_lore"));
                break;
            case OAK_FENCE:
                displayName = plugin.getConfigManager().getLang("gui.structure_layout.block.oak_fence_name");
                lore.addAll(plugin.getConfigManager().getStringList("gui.structure_layout.block.oak_fence_lore"));
                break;
            case PISTON:
                displayName = plugin.getConfigManager().getLang("gui.structure_layout.block.piston_name");
                lore.addAll(plugin.getConfigManager().getStringList("gui.structure_layout.block.piston_lore"));
                break;
            case SMOOTH_STONE:
                displayName = plugin.getConfigManager().getLang("gui.structure_layout.block.smooth_stone_name");
                lore.addAll(plugin.getConfigManager().getStringList("gui.structure_layout.block.smooth_stone_lore"));
                break;
            case GLASS:
                displayName = plugin.getConfigManager().getLang("gui.structure_layout.block.glass_name");
                lore.addAll(plugin.getConfigManager().getStringList("gui.structure_layout.block.glass_lore"));
                break;
            default:
                displayName = plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_BLOCK_GENERIC_NAME,
                        "%material%", formatMaterialName(material));
                lore.addAll(plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_BLOCK_GENERIC_LORE));
        }

        lore.add("");
        lore.add(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_BLOCK_COORDINATES,
                "%x%", String.valueOf(offset.getX()),
                "%y%", String.valueOf(offset.getY()),
                "%z%", String.valueOf(offset.getZ())));

        // 如果是主方块（原点），特别标记
        if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
            lore.add(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_BLOCK_MAIN_MARKER));
        }

        return new ItemBuilder(displayMaterial)
                .displayName(displayName)
                .lore(lore)
                .build();
    }

    private Material getDisplayMaterial(Material originalMaterial) {
        switch (originalMaterial) {
            case FIRE:
                return Material.FLINT_AND_STEEL; // 用打火石代表火
            default:
                if (originalMaterial.isItem()) {
                    return originalMaterial;
                } else {
                    return getAlternativeDisplayMaterial(originalMaterial);
                }
        }
    }

    /**
     * 为无法作为物品的方块提供替代显示材料
     */
    private Material getAlternativeDisplayMaterial(Material material) {
        return switch (material) {
            case FIRE -> Material.FLINT_AND_STEEL;
            case WATER -> Material.WATER_BUCKET;
            case LAVA -> Material.LAVA_BUCKET;
            default -> Material.BARRIER; // 默认使用屏障方块
        };
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
                String displayName = plugin.getConfigManager().getLang(canGoUp ?
                        Messages.GUI_STRUCTURE_LAYOUT_LAYER_UP_ENABLED :
                        Messages.GUI_STRUCTURE_LAYOUT_LAYER_UP_DISABLED);

                List<String> lore;
                if (canGoUp) {
                    lore = new ArrayList<>(plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_LAYER_UP_ENABLED_LORE));
                    for (int i = 0; i < lore.size(); i++) {
                        String line = lore.get(i);
                        line = line.replace("%current%", String.valueOf(currentLayer));
                        line = line.replace("%next%", String.valueOf(currentLayer + 1));
                        lore.set(i, line);
                    }
                } else {
                    lore = new ArrayList<>(plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_LAYER_UP_DISABLED_LORE));
                }

                return new ItemBuilder(canGoUp ? Material.LIME_STAINED_GLASS : Material.GRAY_STAINED_GLASS)
                        .displayName(displayName)
                        .lore(lore)
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
                String displayName = plugin.getConfigManager().getLang(canGoDown ?
                        Messages.GUI_STRUCTURE_LAYOUT_LAYER_DOWN_ENABLED :
                        Messages.GUI_STRUCTURE_LAYOUT_LAYER_DOWN_DISABLED);

                List<String> lore;
                if (canGoDown) {
                    lore = plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_LAYER_DOWN_ENABLED_LORE);
                    for (int i = 0; i < lore.size(); i++) {
                        String line = lore.get(i);
                        line = line.replace("%current%", String.valueOf(currentLayer));
                        line = line.replace("%next%", String.valueOf(currentLayer - 1));
                        lore.set(i, line);
                    }
                } else {
                    lore = plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_LAYER_DOWN_DISABLED_LORE);
                }

                return new ItemBuilder(canGoDown ? Material.RED_STAINED_GLASS : Material.GRAY_STAINED_GLASS)
                        .displayName(displayName)
                        .lore(lore)
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
        List<String> layerInfoLore = plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_LAYER_INFO_LORE);
        for (int i = 0; i < layerInfoLore.size(); i++) {
            String line = layerInfoLore.get(i);
            line = line.replace("%current%", String.valueOf(currentLayer));
            line = line.replace("%min%", String.valueOf(minY));
            line = line.replace("%max%", String.valueOf(maxY));
            line = line.replace("%total%", String.valueOf(maxY - minY + 1));
            layerInfoLore.set(i, line);
        }

        setComponent(LAYER_INFO_SLOT, new StaticComponent(
                new ItemBuilder(Material.PAPER)
                        .displayName(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_LAYER_INFO_NAME))
                        .lore(layerInfoLore)
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
        List<String> structureInfoLore = plugin.getConfigManager().getStringList(Messages.GUI_STRUCTURE_LAYOUT_STRUCTURE_INFO_LORE);
        for (int i = 0; i < structureInfoLore.size(); i++) {
            String line = structureInfoLore.get(i);
            line = line.replace("%name%", structure.getDisplayName());
            line = line.replace("%blocks%", String.valueOf(structure.getBlockCount()));
            line = line.replace("%dimensions%",
                    structure.getBoundingBox().getWidth() + "x" +
                            structure.getBoundingBox().getHeight() + "x" +
                            structure.getBoundingBox().getDepth());
            structureInfoLore.set(i, line);
        }

        setComponent(INFO_SLOT, new StaticComponent(
                new ItemBuilder(Material.BOOK)
                        .displayName(plugin.getConfigManager().getLang(Messages.GUI_STRUCTURE_LAYOUT_STRUCTURE_INFO_NAME))
                        .lore(structureInfoLore)
                        .build()
        ));
    }
}