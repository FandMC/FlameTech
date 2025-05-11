package cn.fandmc.structure;

import cn.fandmc.config.Config;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Structure {
    private final String id;
    private final String name;

    public Structure(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract boolean checkStructure(Location coreLocation);

    public void onStructureCreated(Player player, Location coreLocation) {
        String message = String.format(Config.BLOCKSTRUCTURE_CREATED_SUCCESS, name);
        player.sendMessage(message);
    }

    protected final void saveStructureLocation(Location loc) {
        StructureManager.trackStructureLocation(loc);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public abstract List<int[]> getStructureLayout();
}
