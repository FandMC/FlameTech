package cn.fandmc.structure;

import cn.fandmc.recipe.Recipe;
import org.bukkit.Location;

public interface StructureValidator {
    boolean matchesStructure(Location location, String requiredStructure);
    void processStructureCraft(Location location, Recipe recipe);
}
