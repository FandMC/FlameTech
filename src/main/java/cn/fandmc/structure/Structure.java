package cn.fandmc.structure;

import cn.fandmc.util.LangUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Structure {
    private final String id;
    private final String name;

    public Structure(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract boolean checkStructure(Location coreLocation);

    public void onStructureCreated(Player player, Location coreLocation) {
        player.sendMessage(String.format(LangUtil.get("BlockStructure.Created.Success"), name));
    }

    protected final void saveStructureLocation(Location loc) {
        StructureManager.trackStructureLocation(loc);
    }

    public String getId() { return id; }
    public String getName() { return name; }

}
