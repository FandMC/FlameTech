package cn.fandmc.structure;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class StructureManager {
    private static final Map<String, Structure> structures = new HashMap<>();
    private static final Map<Location, Structure> activeStructures = Collections.synchronizedMap(new WeakHashMap<>());

    public static void registerStructure(Structure structure) {
        structures.put(structure.getId(), structure);
    }

    public static boolean isValidStructureAt(Location loc, String structureId) {
        Structure structure = detectStructure(loc);
        return structure != null && structure.getId().equals(structureId);
    }

    public static Structure detectStructure(Location loc) {
        for (Structure structure : structures.values()) {
            if (structure.checkStructure(loc)) {
                activeStructures.put(loc.clone(), structure); // 更新缓存
                return structure;
            }
        }
        activeStructures.remove(loc);
        return null;
    }


    public static void handleStructureCreation(Player player, Location coreBlock) {
        Structure structure = detectStructure(coreBlock);
        if (structure != null) {
            structure.onStructureCreated(player,coreBlock);
            trackStructureLocation(coreBlock);
        }
    }

    public static void trackStructureLocation(Location loc) {
        loc = loc.clone();
        loc.setYaw(0);
        loc.setPitch(0);
        activeStructures.put(loc, activeStructures.get(loc));
    }

    public static void handleStructureBreak(Location loc) {
        activeStructures.entrySet().removeIf(entry -> entry.getKey().equals(loc));
    }
}
