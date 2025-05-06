package cn.fandmc.structure;

import cn.fandmc.Main;
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
        player.sendMessage(String.format(getlang("BlockStructure.Created.Success"), name));

    }

    public String getId() { return id; }
    public String getName() { return name; }

    public static String getlang(String config){
        return Main.getconfig().color(config);
    }
}
