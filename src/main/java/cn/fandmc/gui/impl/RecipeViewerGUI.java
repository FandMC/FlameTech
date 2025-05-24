package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.StaticItem;
import cn.fandmc.multiblock.MultiblockManager;
import cn.fandmc.multiblock.MultiblockStructure;
import cn.fandmc.multiblock.BlockOffset;
import cn.fandmc.unlock.UnlockManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeViewerGUI extends GUI {
    private final String multiblockId;
    private static final Map<String, RecipeViewerGUI> instances = new HashMap<>();

    private static final int[] STRUCTURE_DISPLAY_SLOTS = {
            5, 6, 7,
            14, 15, 16,
            23, 24, 25
    };

    private static final int STRUCTURE_INFO_SLOT = 10;
    private static final int VIEW_RECIPES_SLOT = 8;
    private static final int BACK_SLOT = 18;

    private RecipeViewerGUI(Main plugin, String multiblockId) {
        super(plugin, "recipe_viewer_" + multiblockId, 27,
                plugin.getConfigManager().getLang("gui.recipe_viewer.title")
                        .replace("%machine%", plugin.getConfigManager().getLang("multiblock." + multiblockId + ".name")));
        this.multiblockId = multiblockId;
        setParentGUI("basic_machines");
    }

    public static RecipeViewerGUI getInstance(Main plugin, String multiblockId) {
        return instances.computeIfAbsent(multiblockId, k -> {
            RecipeViewerGUI gui = new RecipeViewerGUI(plugin, multiblockId);
            GUIManager.registerGUI(gui);
            return gui;
        });
    }

    @Override
    protected void buildGUI() {
        clearComponents();

        displayMultiblockStructure();

        setComponent(STRUCTURE_INFO_SLOT, new StaticItem(createStructureInfo()));

        setComponent(VIEW_RECIPES_SLOT, new GUIComponent() {
            @Override
            public ItemStack item() {
                ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§a查看配方");
                    List<String> lore = Arrays.asList(
                            "§7查看此结构的所有配方",
                            "§7包含合成材料和产出物",
                            "",
                            "§e点击打开配方列表"
                    );
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                return item;
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                StructureRecipesGUI recipesGUI = StructureRecipesGUI.getInstance(plugin, multiblockId);
                recipesGUI.setParentGUI("recipe_viewer_" + multiblockId);
                recipesGUI.open(player);
            }
        });

        setupBackButton(BACK_SLOT);
    }

    private ItemStack createStructureInfo() {
        MultiblockStructure structure = MultiblockManager.getInstance().getStructure(multiblockId);
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6结构信息");
            List<String> lore = new ArrayList<>();
            lore.add("§7结构名称: §e" + (structure != null ? structure.getDisplayName() : "未知"));
            lore.add("§7方块数量: §e" + (structure != null ? structure.getStructure().size() : 0));
            lore.add("");
            lore.add("§7搭建说明:");
            lore.add("§7• 按照右侧显示的布局搭建");
            lore.add("§7• 搭建完成后右键主方块");
            lore.add("");
            lore.add("§a右侧显示具体摆放位置");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void displayMultiblockStructure() {
        MultiblockStructure structure = MultiblockManager.getInstance().getStructure(multiblockId);
        if (structure == null) {
            displayError();
            return;
        }

        Map<BlockOffset, Material> structureMap = structure.getStructure();

        if ("enhanced_crafting_table".equals(multiblockId)) {
            displayVerticalStructure(structureMap);
        } else {
            displayGeneralStructure(structureMap);
        }
    }

    private void displayVerticalStructure(Map<BlockOffset, Material> structureMap) {
        Map<Integer, List<Map.Entry<BlockOffset, Material>>> layers = new HashMap<>();

        for (Map.Entry<BlockOffset, Material> entry : structureMap.entrySet()) {
            int y = entry.getKey().y;
            layers.computeIfAbsent(y, k -> new ArrayList<>()).add(entry);
        }

        int minY = layers.keySet().stream().mapToInt(Integer::intValue).min().orElse(0);
        int maxY = layers.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        int yRange = maxY - minY + 1;

        int startRow = (3 - yRange) / 2;
        if (startRow < 0) startRow = 0;

        int currentRow = startRow;
        for (int y = maxY; y >= minY && currentRow < 3; y--) {
            List<Map.Entry<BlockOffset, Material>> layerBlocks = layers.get(y);
            if (layerBlocks != null && !layerBlocks.isEmpty()) {
                int centerCol = 1; // 中间列
                int slotIndex = currentRow * 3 + centerCol;

                if (slotIndex < STRUCTURE_DISPLAY_SLOTS.length) {
                    Map.Entry<BlockOffset, Material> entry = layerBlocks.get(0);
                    BlockOffset offset = entry.getKey();
                    Material material = entry.getValue();

                    ItemStack displayItem = new ItemStack(material);
                    ItemMeta meta = displayItem.getItemMeta();
                    if (meta != null) {
                        String layerName = y == 0 ? "§e主层" : (y > 0 ? "§a上层 +" + y : "§c下层 " + y);
                        meta.setDisplayName(layerName + " - " + getMaterialDisplayName(material));

                        List<String> lore = new ArrayList<>();
                        lore.add("§7坐标: §eX=" + offset.x + " Y=" + offset.y + " Z=" + offset.z);
                        lore.add("§7方块: §e" + material.name());
                        lore.add("");

                        if (y == 0) {
                            lore.add("§a这是主方块");
                            lore.add("§7右键此方块激活结构");
                        } else if (y > 0) {
                            lore.add("§7在主方块上方 " + y + " 格");
                        } else {
                            lore.add("§7在主方块下方 " + Math.abs(y) + " 格");
                        }

                        meta.setLore(lore);
                        displayItem.setItemMeta(meta);
                    }

                    setComponent(STRUCTURE_DISPLAY_SLOTS[slotIndex], new StaticItem(displayItem));
                }
                currentRow++;
            }
        }
    }

    private void displayGeneralStructure(Map<BlockOffset, Material> structureMap) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockOffset offset : structureMap.keySet()) {
            minX = Math.min(minX, offset.x);
            maxX = Math.max(maxX, offset.x);
            minY = Math.min(minY, offset.y);
            maxY = Math.max(maxY, offset.y);
            minZ = Math.min(minZ, offset.z);
            maxZ = Math.max(maxZ, offset.z);
        }

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int depth = maxZ - minZ + 1;

        if (width <= 3 && depth <= 3) {
            displaySmallStructureIn3x3(structureMap, minX, minY, minZ);
        } else {
            displayLayeredStructure(structureMap, minX, minY, minZ, maxY);
        }
    }

    private void displaySmallStructureIn3x3(Map<BlockOffset, Material> structureMap, int minX, int minY, int minZ) {
        for (int slot : STRUCTURE_DISPLAY_SLOTS) {
            removeComponent(slot);
        }

        int maxX = structureMap.keySet().stream().mapToInt(offset -> offset.x).max().orElse(minX);
        int maxY = structureMap.keySet().stream().mapToInt(offset -> offset.y).max().orElse(minY);
        int maxZ = structureMap.keySet().stream().mapToInt(offset -> offset.z).max().orElse(minZ);

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int depth = maxZ - minZ + 1;

        int offsetX = (3 - width) / 2;
        int offsetZ = (3 - depth) / 2;

        for (Map.Entry<BlockOffset, Material> entry : structureMap.entrySet()) {
            BlockOffset offset = entry.getKey();
            Material material = entry.getValue();

            int gridX = (offset.x - minX) + offsetX;
            int gridZ = (offset.z - minZ) + offsetZ;

            if (gridX >= 0 && gridX < 3 && gridZ >= 0 && gridZ < 3) {
                int slotIndex = gridZ * 3 + gridX; // Z是行，X是列

                if (slotIndex < STRUCTURE_DISPLAY_SLOTS.length) {
                    ItemStack displayItem = new ItemStack(material);
                    ItemMeta meta = displayItem.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName("§e" + getMaterialDisplayName(material));

                        List<String> lore = new ArrayList<>();
                        lore.add("§7网格位置: §e第" + (gridZ + 1) + "行 第" + (gridX + 1) + "列");
                        lore.add("§7坐标: §eX=" + offset.x + " Y=" + offset.y + " Z=" + offset.z);
                        lore.add("§7方块类型: §e" + material.name());

                        if (offset.y == 0 && offset.x == 0 && offset.z == 0) {
                            lore.add("");
                            lore.add("§a这是主方块");
                            lore.add("§7右键此方块激活结构");
                        }

                        if (offset.y != 0) {
                            lore.add("");
                            if (offset.y > 0) {
                                lore.add("§7在主层上方 " + offset.y + " 格");
                            } else {
                                lore.add("§7在主层下方 " + Math.abs(offset.y) + " 格");
                            }
                        }

                        meta.setLore(lore);
                        displayItem.setItemMeta(meta);
                    }

                    setComponent(STRUCTURE_DISPLAY_SLOTS[slotIndex], new StaticItem(displayItem));
                }
            }
        }
    }

    private void displayLayerIn3x3(List<Map.Entry<BlockOffset, Material>> layerBlocks, int layerY, int minX, int minZ) {
        for (Map.Entry<BlockOffset, Material> entry : layerBlocks) {
            BlockOffset offset = entry.getKey();
            Material material = entry.getValue();

            int gridX = offset.x - minX;
            int gridZ = offset.z - minZ;

            if (gridX >= 0 && gridX < 3 && gridZ >= 0 && gridZ < 3) {
                int slotIndex = gridZ * 3 + gridX; // Z是行，X是列

                if (slotIndex < STRUCTURE_DISPLAY_SLOTS.length) {
                    ItemStack displayItem = new ItemStack(material);
                    ItemMeta meta = displayItem.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName("§e" + getMaterialDisplayName(material));

                        List<String> lore = new ArrayList<>();
                        lore.add("§7网格位置: §e第" + (gridZ + 1) + "行 第" + (gridX + 1) + "列");
                        lore.add("§7坐标: §eX=" + offset.x + " Y=" + offset.y + " Z=" + offset.z);
                        lore.add("§7方块类型: §e" + material.name());

                        if (offset.y == 0 && offset.x == 0 && offset.z == 0) {
                            lore.add("");
                            lore.add("§a这是主方块");
                            lore.add("§7右键此方块激活结构");
                        }

                        if (layerY != 0) {
                            lore.add("");
                            if (layerY > 0) {
                                lore.add("§7在主层上方 " + layerY + " 格");
                            } else {
                                lore.add("§7在主层下方 " + Math.abs(layerY) + " 格");
                            }
                        }

                        meta.setLore(lore);
                        displayItem.setItemMeta(meta);
                    }

                    setComponent(STRUCTURE_DISPLAY_SLOTS[slotIndex], new StaticItem(displayItem));
                }
            }
        }
    }

    private void displayLayeredStructure(Map<BlockOffset, Material> structureMap, int minX, int minY, int minZ, int maxY) {
        Map<Integer, List<Map.Entry<BlockOffset, Material>>> layers = new HashMap<>();

        for (Map.Entry<BlockOffset, Material> entry : structureMap.entrySet()) {
            int y = entry.getKey().y;
            layers.computeIfAbsent(y, k -> new ArrayList<>()).add(entry);
        }

        List<Integer> sortedYLevels = layers.keySet().stream()
                .sorted(Collections.reverseOrder()) // 从高到低
                .limit(3)
                .collect(Collectors.toList());

        int row = 0;
        for (Integer y : sortedYLevels) {
            if (row >= 3) break;

            List<Map.Entry<BlockOffset, Material>> layerBlocks = layers.get(y);
            if (!layerBlocks.isEmpty()) {
                Material representativeMaterial = getMostCommonMaterial(
                        layerBlocks.stream().map(Map.Entry::getValue).collect(Collectors.toList())
                );

                int slotIndex = row * 3 + 1;

                ItemStack layerItem = new ItemStack(representativeMaterial);
                ItemMeta meta = layerItem.getItemMeta();
                if (meta != null) {
                    String layerName = y == 0 ? "§e主层" : (y > 0 ? "§a上层 +" + y : "§c下层 " + y);
                    meta.setDisplayName(layerName);

                    List<String> lore = new ArrayList<>();
                    lore.add("§7Y坐标: " + y);
                    lore.add("§7包含 " + layerBlocks.size() + " 个方块");

                    Map<Material, Integer> materialCount = new HashMap<>();
                    for (Map.Entry<BlockOffset, Material> entry : layerBlocks) {
                        Material mat = entry.getValue();
                        materialCount.put(mat, materialCount.getOrDefault(mat, 0) + 1);
                    }

                    lore.add("");
                    lore.add("§7方块组成:");
                    for (Map.Entry<Material, Integer> count : materialCount.entrySet()) {
                        lore.add("§7• " + getMaterialDisplayName(count.getKey()) + " x" + count.getValue());
                    }

                    meta.setLore(lore);
                    layerItem.setItemMeta(meta);
                }

                setComponent(STRUCTURE_DISPLAY_SLOTS[slotIndex], new StaticItem(layerItem));
                row++;
            }
        }
    }

    private void displayError() {
        ItemStack error = new ItemStack(Material.BARRIER);
        ItemMeta meta = error.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c结构加载失败");
            meta.setLore(Arrays.asList(
                    "§7无法找到结构定义",
                    "§7结构ID: §e" + multiblockId,
                    "",
                    "§c请联系管理员检查插件配置"
            ));
            error.setItemMeta(meta);
        }
        setComponent(STRUCTURE_DISPLAY_SLOTS[0], new StaticItem(error));
    }

    private String getMaterialDisplayName(Material material) {
        switch (material) {
            case CRAFTING_TABLE:
                return "工作台";
            case DISPENSER:
                return "发射器";
            case STONE_BRICKS:
                return "石砖";
            case IRON_BLOCK:
                return "铁块";
            case DIAMOND_BLOCK:
                return "钻石块";
            default:
                return material.name().toLowerCase().replace("_", " ");
        }
    }

    private Material getMostCommonMaterial(Collection<Material> materials) {
        Map<Material, Integer> count = new HashMap<>();
        for (Material mat : materials) {
            count.put(mat, count.getOrDefault(mat, 0) + 1);
        }

        return count.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Material.STONE);
    }
}