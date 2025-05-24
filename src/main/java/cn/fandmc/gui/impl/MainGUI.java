package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.item.main.*;
import cn.fandmc.gui.templates.PaginatedGUI;

public class MainGUI extends PaginatedGUI {
    private static MainGUI instance;

    public MainGUI(Main plugin) {
        super(plugin, "main", plugin.getConfigManager().getLang("guide_book.display_name"));
        initializeItems();
    }

    public static MainGUI getInstance(Main plugin) {
        if (instance == null) {
            instance = new MainGUI(plugin);
            GUIManager.registerGUI(instance);
        }
        return instance;
    }

    private void initializeItems() {
        clearPageItems();
        addPageItem(new BasicMachineButton());
        addPageItem(new ToolsButton());
    }

    @Override
    protected void setupControlButtons() {
        super.setupControlButtons();
    }
}