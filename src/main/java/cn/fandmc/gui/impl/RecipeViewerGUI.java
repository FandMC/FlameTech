package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.StaticItem;
import cn.fandmc.multiblock.MultiblockManager;
import cn.fandmc.multiblock.MultiblockStructure;
import cn.fandmc.multiblock.BlockOffset;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RecipeViewerGUI extends GUI {
    private final String multiblockId;
    private static final Map<String, RecipeViewerGUI> instances = new HashMap<>();

    private static final int[] RECIPE_SLOTS = {5, 6, 7, 14, 15, 16, 23, 24, 25};
    private static final int TYPE_SLOT = 11;
    private static final int ACTION_SLOT = 8;
    private static final int BACK_SLOT = 18;

    private RecipeViewerGUI(Main plugin, String multiblockId) {
        super(plugin, "recipe_viewer_" + multiblockId, 27,
                plugin.getConfigManager().getLang("gui.recipe_viewer.title")
                        .replace("%machine%", plugin.getConfigManager().getLang("multiblock." + multiblockId + ".name")));
        this.multiblockId = multiblockId;
    }

    public static RecipeViewerGUI getInstance(Main plugin, String multiblockId) {
        String key = multiblockId;
        if (!instances.containsKey(key)) {
            RecipeViewerGUI gui = new RecipeViewerGUI(plugin, multiblockId);
            instances.put(key, gui);
            GUIManager.registerGUI(gui);
        }
        return instances.get(key);
    }

    @Override
    protected void buildGUI() {
        clearComponents();

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }

        for (int i = 0; i < 27; i++) {
            boolean isRecipeSlot = false;
            for (int slot : RECIPE_SLOTS) {
                if (i == slot) {
                    isRecipeSlot = true;
                    break;
                }
            }
            if (!isRecipeSlot && i != TYPE_SLOT && i != ACTION_SLOT && i != BACK_SLOT) {
                setComponent(i, new StaticItem(border));
            }
        }

        displayMultiblockStructure();

        ItemStack typeIndicator = new ItemStack(Material.STONE_BRICKS);
        ItemMeta typeMeta = typeIndicator.getItemMeta();
        if (typeMeta != null) {
            typeMeta.setDisplayName(plugin.getConfigManager().getLang("gui.recipe_viewer.structure_type"));
            typeMeta.setLore(plugin.getConfigManager().getStringList("gui.recipe_viewer.structure_type_lore"));
            typeIndicator.setItemMeta(typeMeta);
        }
        setComponent(TYPE_SLOT, new StaticItem(typeIndicator));

        setComponent(ACTION_SLOT, new GUIComponent() {
            @Override
            public ItemStack item() {
                ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(plugin.getConfigManager().getLang("gui.recipe_viewer.view_recipes"));
                    meta.setLore(plugin.getConfigManager().getStringList("gui.recipe_viewer.view_recipes_lore"));
                    item.setItemMeta(meta);
                }
                return item;
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                StructureRecipesGUI recipesGUI = StructureRecipesGUI.getInstance(plugin, multiblockId);
                recipesGUI.open(player);
            }
        });

        setComponent(BACK_SLOT, new GUIComponent() {
            @Override
            public ItemStack item() {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(plugin.getConfigManager().getLang("gui.common.back"));
                    item.setItemMeta(meta);
                }
                return item;
            }

            @Override
            public void onClick(Player player, InventoryClickEvent event) {
                GUIManager.openGUI(player, "basic_machines");
            }
        });
    }

    private void displayMultiblockStructure() {
        MultiblockStructure structure = MultiblockManager.getInstance().getStructure(multiblockId);
        if (structure == null) return;

        Map<BlockOffset, Material> structureMap = structure.getStructure();

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

        if (width <= 3 && depth <= 3 && height <= 3) {
            displaySmallStructure(structureMap, minX, minY, minZ);
        } else {
            displayLayeredStructure(structureMap, minX, minY, minZ, maxY);
        }
    }

    private void displaySmallStructure(Map<BlockOffset, Material> structureMap, int minX, int minY, int minZ) {
        Material[][][] display = new Material[3][3][3];

        for (Map.Entry<BlockOffset, Material> entry : structureMap.entrySet()) {
            BlockOffset offset = entry.getKey();
            Material material = entry.getValue();

            int x = offset.x - minX;
            int y = offset.y - minY;
            int z = offset.z - minZ;

            if (x >= 0 && x < 3 && y >= 0 && y < 3 && z >= 0 && z < 3) {
                display[y][z][x] = material;
            }
        }

        int slotIndex = 0;
        for (int y = 2; y >= 0; y--) {
            for (int z = 0; z < 3; z++) {
                for (int x = 0; x < 3; x++) {
                    if (slotIndex < RECIPE_SLOTS.length && display[y][z][x] != null) {
                        ItemStack item = new ItemStack(display[y][z][x]);
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            List<String> lore = new ArrayList<>();
                            lore.add("§7位置: §eX=" + x + " Y=" + y + " Z=" + z);
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                        }
                        setComponent(RECIPE_SLOTS[slotIndex], new StaticItem(item));
                    }
                    slotIndex++;
                    if (slotIndex >= RECIPE_SLOTS.length) return;
                }
            }
        }
    }

    private void displayLayeredStructure(Map<BlockOffset, Material> structureMap, int minX, int minY, int minZ, int maxY) {
        List<ItemStack> layers = new ArrayList<>();

        for (int y = minY; y <= maxY; y++) {
            Map<String, Material> layer = new HashMap<>();

            for (Map.Entry<BlockOffset, Material> entry : structureMap.entrySet()) {
                BlockOffset offset = entry.getKey();
                if (offset.y == y) {
                    layer.put(offset.x + "," + offset.z, entry.getValue());
                }
            }

            if (!layer.isEmpty()) {
                Material representativeMaterial = getMostCommonMaterial(layer.values());
                ItemStack layerItem = new ItemStack(representativeMaterial);
                ItemMeta meta = layerItem.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§e第 " + (y - minY + 1) + " 层");
                    List<String> lore = new ArrayList<>();
                    lore.add("§7Y = " + y);
                    lore.add("§7包含 " + layer.size() + " 个方块");

                    Map<Material, Integer> materialCount = new HashMap<>();
                    for (Material mat : layer.values()) {
                        materialCount.put(mat, materialCount.getOrDefault(mat, 0) + 1);
                    }

                    lore.add("");
                    for (Map.Entry<Material, Integer> count : materialCount.entrySet()) {
                        lore.add("§7- " + count.getKey().name() + " x" + count.getValue());
                    }

                    meta.setLore(lore);
                    layerItem.setItemMeta(meta);
                }
                layers.add(layerItem);
            }
        }

        for (int i = 0; i < Math.min(layers.size(), RECIPE_SLOTS.length); i++) {
            setComponent(RECIPE_SLOTS[i], new StaticItem(layers.get(i)));
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