package cn.fandmc.structure;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Structure {
    private final String id;
    private final String name;

    public Structure(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract boolean checkStructure(Location coreBlock);

    public void onStructureCreated(Player player, Location coreBlock) {
        player.sendMessage("§a成功创建 " + name + " 结构！");
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
