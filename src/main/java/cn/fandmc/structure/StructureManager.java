package cn.fandmc.structure;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

public class StructureManager {
    private static final Map<String, Structure> structures = new HashMap<>();
    private static final Map<Location, Structure> activeStructures = new HashMap<>();

    public static void registerStructure(Structure structure) {
        structures.put(structure.getId(), structure);
    }

    public static Structure detectStructure(Location loc) {
        if (activeStructures.containsKey(loc)) {
            return activeStructures.get(loc);
        }

        for (Structure structure : structures.values()) {
            if (structure.checkStructure(loc)) {
                activeStructures.put(loc, structure);
                return structure;
            }
        }
        return null;
    }

    public static void handleStructureCreation(Player player, Location coreBlock) {
        Structure structure = detectStructure(coreBlock);
        if (structure != null) {
            structure.onStructureCreated(player, coreBlock);
        }
    }

    public static void handleStructureBreak(Location loc) {
        if (activeStructures.containsKey(loc)) {
            activeStructures.remove(loc);
        }
    }
}
