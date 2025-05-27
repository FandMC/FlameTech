package cn.fandmc.flametech.materials.manager;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.materials.base.Material;
import cn.fandmc.flametech.materials.impl.*;
import cn.fandmc.flametech.utils.MessageUtils;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MaterialManager {

    private final Main plugin;
    // 使用LinkedHashMap保持注册顺序
    private final Map<String, Material> materials = new LinkedHashMap<>();

    public MaterialManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerDefaultMaterials() {
        try {
            // 清空现有材料
            materials.clear();

            // 按逻辑顺序注册材料 - 确保顺序稳定

            // 1. 基础原料
            registerMaterial(new OreDust(plugin));

            // 2. 金属粉末类 (按字母顺序)
            registerMaterial(new AluminumDust(plugin));
            registerMaterial(new ChromeDust(plugin));
            registerMaterial(new CoalDust(plugin));
            registerMaterial(new CopperDust(plugin));
            registerMaterial(new GoldDust(plugin));
            registerMaterial(new IronDust(plugin));
            registerMaterial(new SilverDust(plugin));
            registerMaterial(new TinDust(plugin));
            registerMaterial(new ZincDust(plugin));

            // 3. 金锭纯度系列（按纯度从低到高）
            registerMaterial(new GoldIngot10(plugin));
            registerMaterial(new GoldIngot20(plugin));
            registerMaterial(new GoldIngot30(plugin));
            registerMaterial(new GoldIngot40(plugin));
            registerMaterial(new GoldIngot50(plugin));
            registerMaterial(new GoldIngot60(plugin));
            registerMaterial(new GoldIngot70(plugin));
            registerMaterial(new GoldIngot80(plugin));
            registerMaterial(new GoldIngot90(plugin));
            registerMaterial(new GoldIngot100(plugin));

            // 4. 合金
            registerMaterial(new BronzeIngot(plugin));

            // 5. 硅材料系列
            registerMaterial(new RawSilicon(plugin));
            registerMaterial(new Silicon(plugin));

            // 6. 人造宝石系列（按字母顺序）
            registerMaterial(new ArtificialDiamond(plugin));
            registerMaterial(new ArtificialEmerald(plugin));
            registerMaterial(new ArtificialSapphire(plugin));

            MessageUtils.logInfo("成功注册了 " + materials.size() + " 种材料");

            // 记录注册顺序用于调试
            MessageUtils.logDebug("材料注册顺序: " + String.join(", ", materials.keySet()));

        } catch (Exception e) {
            MessageUtils.logError("注册默认材料失败: " + e.getMessage());
            throw e;
        }
    }

    public boolean registerMaterial(Material material) {
        if (material == null) {
            MessageUtils.logWarning("尝试注册空材料");
            return false;
        }

        String materialId = material.getMaterialId();
        if (materials.containsKey(materialId)) {
            MessageUtils.logWarning("材料ID已存在: " + materialId);
            return false;
        }

        materials.put(materialId, material);
        MessageUtils.logDebug("注册材料: " + materialId + " (" + material.getDisplayName() + ")");
        return true;
    }

    public Optional<Material> getMaterial(String materialId) {
        return Optional.ofNullable(materials.get(materialId));
    }

    public boolean isMaterial(ItemStack item) {
        return getMaterialFromStack(item).isPresent();
    }

    public Optional<Material> getMaterialFromStack(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return Optional.empty();
        }

        for (Material material : materials.values()) {
            try {
                if (material.isMaterial(item)) {
                    return Optional.of(material);
                }
            } catch (Exception e) {
                MessageUtils.logWarning("检查材料时发生错误 (" + material.getMaterialId() + "): " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<ItemStack> createMaterial(String materialId) {
        return getMaterial(materialId).map(material -> {
            try {
                return material.createItem();
            } catch (Exception e) {
                MessageUtils.logError("创建材料 " + materialId + " 时发生错误: " + e.getMessage());
                return null;
            }
        });
    }

    public Optional<ItemStack> createMaterial(String materialId, int amount) {
        return getMaterial(materialId).map(material -> {
            try {
                return material.createItem(amount);
            } catch (Exception e) {
                MessageUtils.logError("创建材料 " + materialId + " (数量: " + amount + ") 时发生错误: " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * 获取所有材料，保持注册顺序 - 修复版本
     */
    public Collection<Material> getAllMaterials() {
        // 返回LinkedHashMap的values()保持顺序
        // 创建副本以避免并发修改，但保持顺序
        List<Material> orderedMaterials = new ArrayList<>();

        // 按照LinkedHashMap的插入顺序遍历
        for (Material material : materials.values()) {
            orderedMaterials.add(material);
        }

        MessageUtils.logDebug("MaterialManager.getAllMaterials: 返回顺序 (前5个): " +
                orderedMaterials.stream()
                        .limit(5)
                        .map(Material::getMaterialId)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("none"));

        return orderedMaterials;
    }

    /**
     * 获取所有材料的有序列表
     */
    public List<Material> getAllMaterialsList() {
        return new ArrayList<>(materials.values());
    }

    /**
     * 获取按类别分组的材料
     */
    public Map<String, List<Material>> getMaterialsByCategory() {
        Map<String, List<Material>> categorized = new LinkedHashMap<>();

        for (Material material : materials.values()) {
            String category = determineMaterialCategory(material);
            categorized.computeIfAbsent(category, k -> new ArrayList<>()).add(material);
        }

        return categorized;
    }

    /**
     * 根据材料ID确定类别
     */
    private String determineMaterialCategory(Material material) {
        String id = material.getMaterialId();

        if (id.contains("dust")) {
            return "dusts";
        } else if (id.contains("ingot")) {
            return "ingots";
        } else if (id.contains("artificial")) {
            return "gems";
        } else if (id.equals("ore_dust")) {
            return "raw_materials";
        } else if (id.contains("silicon")) {
            return "processed";
        } else if (id.contains("bronze")) {
            return "alloys";
        } else {
            return "other";
        }
    }

    public int getRegisteredMaterialCount() {
        return materials.size();
    }

    public void clearAllMaterials() {
        materials.clear();
        MessageUtils.logDebug("清空所有材料");
    }

    public void reload() {
        MessageUtils.logInfo("重载材料管理器...");
        clearAllMaterials();
        registerDefaultMaterials();
        MessageUtils.logInfo("材料管理器重载完成");
    }

    /**
     * 获取材料统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total_materials", materials.size());

        // 按类别统计
        Map<String, List<Material>> byCategory = getMaterialsByCategory();
        Map<String, Integer> categoryStats = new LinkedHashMap<>();
        for (Map.Entry<String, List<Material>> entry : byCategory.entrySet()) {
            categoryStats.put(entry.getKey(), entry.getValue().size());
        }
        stats.put("by_category", categoryStats);

        // 注册顺序
        stats.put("registration_order", new ArrayList<>(materials.keySet()));

        return stats;
    }

    /**
     * 验证所有材料
     */
    public List<String> validateAllMaterials() {
        List<String> errors = new ArrayList<>();

        for (Map.Entry<String, Material> entry : materials.entrySet()) {
            String id = entry.getKey();
            Material material = entry.getValue();

            try {
                // 验证材料ID一致性
                if (!id.equals(material.getMaterialId())) {
                    errors.add("材料ID不一致: 映射=" + id + ", 材料=" + material.getMaterialId());
                }

                // 尝试创建物品
                ItemStack item = material.createItem();
                if (item == null) {
                    errors.add("材料 " + id + " 创建物品失败");
                }

                // 验证材料检测
                if (item != null && !material.isMaterial(item)) {
                    errors.add("材料 " + id + " 无法识别自己创建的物品");
                }

            } catch (Exception e) {
                errors.add("验证材料 " + id + " 时发生错误: " + e.getMessage());
            }
        }

        return errors;
    }

    /**
     * 搜索材料
     */
    public List<Material> searchMaterials(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMaterialsList();
        }

        String lowerKeyword = keyword.toLowerCase();
        List<Material> results = new ArrayList<>();

        for (Material material : materials.values()) {
            if (material.getMaterialId().toLowerCase().contains(lowerKeyword) ||
                    material.getDisplayName().toLowerCase().contains(lowerKeyword)) {
                results.add(material);
            }
        }

        return results;
    }

    /**
     * 获取材料信息的调试字符串
     */
    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("MaterialManager Debug Info:\n");
        sb.append("Total Materials: ").append(materials.size()).append("\n");
        sb.append("Registration Order:\n");

        int index = 0;
        for (Map.Entry<String, Material> entry : materials.entrySet()) {
            sb.append(String.format("  %d. %s (%s)\n",
                    ++index, entry.getKey(), entry.getValue().getDisplayName()));
        }

        return sb.toString();
    }
}