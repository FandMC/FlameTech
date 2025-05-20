package cn.fandmc.gui.templates;

import cn.fandmc.Main;
import cn.fandmc.gui.GUI;
import cn.fandmc.gui.GUIComponent;

import java.util.ArrayList;
import java.util.List;

public class SimpleGUI extends GUI {
    private final List<GUIComponent> simpleItems = new ArrayList<>();

    public SimpleGUI(Main plugin, String name, String title) {
        super(plugin, name, 27, title);
        buildItems();
    }

    @Override
    protected void buildItems() {
        simpleItems.forEach(this::addItem);
    }

    public void addSimpleItem(GUIComponent component) {
        simpleItems.add(component);
    }
}
