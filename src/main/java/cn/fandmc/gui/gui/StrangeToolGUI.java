package cn.fandmc.gui.gui;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Stack;

public class StrangeToolGUI {
    public static void open(Player player) {
        GUI.menuStack.computeIfAbsent(player, k -> new Stack<>())
                .push(new HashMap<>(GUI.components));

        GUI.cleanComponents();
        //GUI.registerComponent(new EnhancedWorkbench());

        player.openInventory(GUI.createGUI(getlang("Item.StrangeTool.Name"), 27));
    }

    public static String getlang(String config){
        return Main.getconfig().color(config);
    }
}

