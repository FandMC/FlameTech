package cn.fandmc.gui.impl;

import cn.fandmc.Main;
import cn.fandmc.gui.GUIManager;
import cn.fandmc.gui.item.machines.EnhancedCraftingTableButton;
import cn.fandmc.gui.templates.SimpleGUI;

public class BasicMachinesGUI extends SimpleGUI {
    private static BasicMachinesGUI instance;

    private BasicMachinesGUI(Main plugin) {
        super(plugin, "basic_machines", 54, plugin.getConfigManager().getLang("gui.basic_machines.title"));
        setParentGUI("main");
    }

    public static BasicMachinesGUI getInstance(Main plugin) {
        if (instance == null) {
            instance = new BasicMachinesGUI(plugin);
            GUIManager.registerGUI(instance);
        }
        return instance;
    }

    @Override
    protected void buildGUI() {
        clearComponents();

        setupBackButton(45);
        addBasicMachines();
    }

    private void addBasicMachines() {
        // 添加增强工作台按钮 - 模板会自动处理解锁状态和位置
        setCenterItem(new EnhancedCraftingTableButton());
    }

    @Override
    public void refresh() {
        buildGUI();
        super.refresh();
    }
}