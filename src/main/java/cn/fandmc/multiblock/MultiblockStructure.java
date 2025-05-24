package cn.fandmc.multiblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import java.util.Map;

public abstract class MultiblockStructure {
    protected final String id;
    protected final String displayName;
    protected final Map<BlockOffset, Material> structure;

    public MultiblockStructure(String id, String displayName, Map<BlockOffset, Material> structure) {
        this.id = id;
        this.displayName = displayName;
        this.structure = structure;
    }

    public boolean checkStructure(Location centerLocation) {
        for (Map.Entry<BlockOffset, Material> entry : structure.entrySet()) {
            BlockOffset offset = entry.getKey();
            Material required = entry.getValue();

            Block block = centerLocation.clone()
                    .add(offset.x, offset.y, offset.z)
                    .getBlock();

            if (block.getType() != required) {
                return false;
            }
        }
        return true;
    }

    public abstract void onActivate(Player player, Location location);
    public abstract boolean canCraft(String recipeId);

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Map<BlockOffset, Material> getStructure() { return structure; }
}
