package cn.fandmc.gui;

import cn.fandmc.Main;
import cn.fandmc.config.Config;
import cn.fandmc.gui.guild.BaseMachine;
import cn.fandmc.gui.guild.PlayerHead;
import cn.fandmc.gui.guild.StrangeTool;
import cn.fandmc.gui.item.BaseMachine.EnhancedWorkbench;
import cn.fandmc.gui.item.BorderItem;
import cn.fandmc.gui.item.StrangeTool.SmeltingPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GUI {
    private static JavaPlugin plugin;
    private static final GUIHolder holder = new GUIHolder();
    static final Map<Player, Stack<String>> guiHistory = new HashMap<>();

    public static void init(JavaPlugin plugin) {
        GUI.plugin = plugin;
        GUIRegistry.registerPage("main", Config.GUI_NAME, 54, false);
        GUIRegistry.registerPage("base_machine", Config.ITEM_BASEMACHINE_NAME, 54, true);
        GUIRegistry.registerPage("strange_tool", Config.ITEM_STRANGETOOL_NAME, 54, true);

        registerDefaultComponents();
        registerListeners();
    }

    private static void registerDefaultComponents() {

        int[] reservedSlots = {0,1,2,3,5,6,7,8,45,46,47,48,49,50,51,52,53};
        for (int slot : reservedSlots) {
            GUIRegistry.registerComponent("main", new BorderItem(slot));
        }

        GUIRegistry.registerComponent("main", new BaseMachine());
        GUIRegistry.registerComponent("main", new StrangeTool());
        GUIRegistry.registerComponent("main", new PlayerHead(4));

        GUIRegistry.registerComponent("base_machine", new EnhancedWorkbench());

        GUIRegistry.registerComponent("strange_tool", new SmeltingPickaxe());
    }

    private static void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    public static void open(Player player, String pageId) {
        GUIRegistry.Page page = GUIRegistry.getPage(pageId);
        if (page == null) return;
        List<GUIComponent> components = new ArrayList<>(page.getComponents());
        components.addAll(getDynamicComponents(player, pageId));
        Stack<String> history = guiHistory.computeIfAbsent(player, k -> new Stack<>());
        if (!history.isEmpty() && !history.peek().equals(pageId)) {
            history.push(pageId);
        }
        Inventory inv = createGUI(page);
        player.openInventory(inv);
    }

    private static List<GUIComponent> getDynamicComponents(Player player, String pageId) {
        List<GUIComponent> result = new ArrayList<>();

        if ("main".equals(pageId)) {
            PlayerHead head = new PlayerHead(4).withPlayer(player);
            result.add(head);
        }

        return result;
    }

    public static class GUIHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }

    private static Inventory createGUI(GUIRegistry.Page page) {
        Inventory inv = Bukkit.createInventory(holder, page.getSize(), page.getTitle());

        List<GUIComponent> components = page.getComponents();
        if (page.isAutoArrange()) {
            autoArrangeComponents(inv, components);
        } else {
            manualArrangeComponents(inv, components);
        }

        return inv;
    }

    private static void autoArrangeComponents(Inventory inv, List<GUIComponent> components) {
        int slot = 0;
        for (GUIComponent comp : components) {
            while (slot < inv.getSize() && isReservedSlot(slot)) {
                slot++;
            }
            if (slot < inv.getSize()) {
                inv.setItem(slot++, comp.createItem());
            }
        }
    }

    private static void manualArrangeComponents(Inventory inv, List<GUIComponent> components) {
        for (GUIComponent comp : components) {
            int slot = comp.getSlot();
            if (slot >= 0 && slot < inv.getSize()) {
                inv.setItem(slot, comp.createItem());
            }
        }
    }

    private static boolean isReservedSlot(int slot) {
        if (slot < 9) return true;
        if (slot >= 45) return true;
        if (slot % 9 == 0 || (slot + 1) % 9 == 0) return true;
        return false;
    }

    public static void refresh(Player player) {
        if (player.getOpenInventory() != null) {
            Stack<String> history = guiHistory.get(player);
            if (history == null || history.isEmpty()) {
                open(player, "main");
                return;
            }
            String currentPage = history.peek();
            open(player, currentPage);
        }
    }

    public static Optional<GUIComponent> getComponentByItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Optional.empty();
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "flametech_item");
        String id = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (id == null) return Optional.empty();

        for (GUIRegistry.Page page : GUIRegistry.pages.values()) {
            for (GUIComponent comp : page.getComponents()) {
                if (comp.id().equals(id)) {
                    return Optional.of(comp);
                }
            }
        }
        return Optional.empty();
    }
}
