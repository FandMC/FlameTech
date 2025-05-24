package cn.fandmc.multiblock;

import cn.fandmc.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class MultiblockManager implements Listener {
    private static MultiblockManager instance;
    private final Main plugin;
    private final Map<String, MultiblockStructure> structures = new HashMap<>();

    private MultiblockManager(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void init(Main plugin) {
        if (instance == null) {
            instance = new MultiblockManager(plugin);
        }
    }

    public static MultiblockManager getInstance() {
        return instance;
    }

    public void registerStructure(MultiblockStructure structure) {
        structures.put(structure.getId(), structure);
        plugin.getLogger().info("已注册多方块结构: " + structure.getDisplayName());
    }

    public MultiblockStructure getStructure(String id) {
        return structures.get(id);
    }

    public boolean hasStructure(String id) {
        return structures.containsKey(id);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();

        for (MultiblockStructure structure : structures.values()) {
            if (structure.checkStructure(location)) {
                event.setCancelled(true);
                structure.onActivate(player, location);
                return;
            }
        }
    }
}