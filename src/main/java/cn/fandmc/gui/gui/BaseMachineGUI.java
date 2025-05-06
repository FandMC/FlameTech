package cn.fandmc.gui.gui;

import cn.fandmc.gui.GUI;
import cn.fandmc.gui.item.BaseMachine.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Stack;

public class BaseMachineGUI {
    public static void open(Player player) {
        GUI.menuStack.computeIfAbsent(player, k -> new Stack<>())
                .push(new HashMap<>(GUI.components));

        GUI.cleanComponents();
        GUI.registerComponent(new EnhancedWorkbench());

        player.openInventory(GUI.createGUI("基础机器", 27));
    }
}

