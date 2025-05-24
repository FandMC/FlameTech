package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.item.tools.ExplosivePickaxeButton;
import cn.fandmc.gui.item.tools.SmeltingPickaxeButton;
import cn.fandmc.gui.templates.SimpleGUI;

public class ToolsGUI extends SimpleGUI {
    private static ToolsGUI instance;

    private ToolsGUI(Main plugin) {
        super(plugin, "tools", 54, plugin.getConfigManager().getLang("gui.tools.title"));
        setParentGUI("main");
    }

    public static ToolsGUI getInstance(Main plugin) {
        if (instance == null) {
            instance = new ToolsGUI(plugin);
            GUIManager.registerGUI(instance);
        }
        return instance;
    }

    @Override
    protected void buildGUI() {
        clearComponents();

        // 设置返回按钮
        setupBackButton(45);

        // 添加工具配方按钮
        addTools();
    }

    private void addTools() {
        // 添加爆炸镐按钮
        setCenterItem(new ExplosivePickaxeButton());

        // 添加熔炼镐按钮
        setCenterItem(new SmeltingPickaxeButton());
    }

    @Override
    public void refresh() {
        buildGUI();
        super.refresh();
    }
}