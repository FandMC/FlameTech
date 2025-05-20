package cn.fandmc.gui;

import cn.fandmc.Main;
import cn.fandmc.gui.templates.PaginatedGUI;
import cn.fandmc.gui.item.main.MainGUIItem;

public class MainGUI extends PaginatedGUI {
    public MainGUI(Main plugin) {
        super(plugin, "main", plugin.getConfigManager().getLang("guide_book.display_name"), 54);
        GUIManager.registerGUI(this);

        for (int i = 0; i < 10; i++) {
            addPageItem(new MainGUIItem());
        }

        refreshPage(null);
    }

    @Override
    protected void buildItems() {
    }
}
