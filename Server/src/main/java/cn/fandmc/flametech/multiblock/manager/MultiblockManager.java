package cn.fandmc.flametech.multiblock.manager;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.managers.BaseManager;
import cn.fandmc.flametech.multiblock.base.MultiblockStructure;
import cn.fandmc.flametech.multiblock.impl.*;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 多方块结构管理器
 */
public class MultiblockManager extends BaseManager<MultiblockStructure> {

    private final Map<String, MultiblockStructure> structures = new HashMap<>();

    public MultiblockManager(Main plugin) {
        super(plugin, "多方块结构管理器");
    }

    /**
     * 注册默认结构
     */
    @Override
    public void registerDefaults() {
        try {
            registerStructure(new EnhancedCraftingTable(plugin));
            registerStructure(new SmeltingFurnace(plugin));
            registerStructure(new OreWasher(plugin));
            registerStructure(new OreSifter(plugin));
            registerStructure(new PressureMachine(plugin));

            logRegistrationSuccess();

        } catch (Exception e) {
            logRegistrationFailure(e);
            throw e;
        }
    }

    /**
     * 注册多方块结构
     */
    public boolean registerStructure(MultiblockStructure structure) {
        if (structure == null) {
            MessageUtils.logWarning("尝试注册空多方块结构");
            return false;
        }

        String structureId = structure.getStructureId();
        if (structures.containsKey(structureId)) {
            MessageUtils.logWarning("ID为 '" + structureId + "' 的多方块结构已存在");
            return false;
        }

        structures.put(structureId, structure);
        //MessageUtils.logInfo("注册多方块结构: " + structure.getDisplayName() +
                //" (ID: " + structureId + ")");
        return true;
    }

    /**
     * 取消注册结构
     */
    public boolean unregisterStructure(String structureId) {
        MultiblockStructure removed = structures.remove(structureId);
        if (removed != null) {
            MessageUtils.logInfo("已取消注册多方块结构: " + structureId);
            return true;
        }
        return false;
    }

    /**
     * 获取多方块结构
     */
    public Optional<MultiblockStructure> getStructure(String structureId) {
        return Optional.ofNullable(structures.get(structureId));
    }

    /**
     * 检查结构是否存在
     */
    public boolean hasStructure(String structureId) {
        return structures.containsKey(structureId);
    }

    /**
     * 处理玩家交互事件
     */
    public boolean handleInteraction(Player player, Location location, PlayerInteractEvent event) {
        if (!ValidationUtils.isValidPlayer(player) || !ValidationUtils.isValidLocation(location)) {
            return false;
        }

        // 检查所有已注册的结构
        for (MultiblockStructure structure : structures.values()) {
            try {
                if (structure.checkStructure(location)) {
                    event.setCancelled(true);
                    structure.onActivate(player, location, event); // 修复：传递正确的参数
                    return true;
                }
            } catch (Exception e) {
                MessageUtils.logError("Error checking multiblock structure " + structure.getStructureId() + ": " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * 获取指定位置的多方块结构
     */
    public Optional<MultiblockStructure> getStructureAtLocation(Location location) {
        if (!ValidationUtils.isValidLocation(location)) {
            return Optional.empty();
        }

        for (MultiblockStructure structure : structures.values()) {
            try {
                if (structure.checkStructure(location)) {
                    return Optional.of(structure);
                }
            } catch (Exception e) {
                MessageUtils.logError("Error checking structure at location: " + e.getMessage());
            }
        }

        return Optional.empty();
    }

    /**
     * 获取所有已注册的结构
     */
    public Map<String, MultiblockStructure> getAllStructures() {
        return new HashMap<>(structures);
    }

    /**
     * 获取已注册结构数量
     */
    @Override
    public int getRegisteredCount() {
        return structures.size();
    }

    /**
     * 清空所有结构
     */
    @Override
    public void clearAll() {
        structures.clear();
        logClearDebug();
    }





    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_structures", structures.size());

        Map<String, Object> structureInfo = new HashMap<>();
        for (MultiblockStructure structure : structures.values()) {
            structureInfo.put(structure.getStructureId(), structure.getStructureInfo());
        }
        stats.put("structures", structureInfo);

        return stats;
    }
}