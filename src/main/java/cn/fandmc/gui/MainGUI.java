package cn.fandmc.gui;

import cn.fandmc.Main;
import cn.fandmc.gui.item.main.BasicMachineButton;
import cn.fandmc.gui.templates.PaginatedGUI;

public class MainGUI extends PaginatedGUI {
    public MainGUI(Main plugin) {
        super(plugin, "main", plugin.getConfigManager().getLang("guide_book.display_name"));

        initializeItems();

        GUIManager.registerGUI(this);
        new BasicMachinesGUI(plugin);
    }

    private void initializeItems() {
        addPageItem(new BasicMachineButton());
    }
}