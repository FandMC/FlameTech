package cn.fandmc.flametech.gui.impl.main;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.PaginatedGUI;
import cn.fandmc.flametech.gui.buttons.machines.*;
import cn.fandmc.flametech.gui.components.NavigationComponent;
import org.bukkit.entity.Player;

/**
 * 基础机器GUI
 */
public class BasicMachinesGUI extends PaginatedGUI {

    public BasicMachinesGUI(Main plugin) {
        super(plugin, "basic_machines",
                plugin.getConfigManager().getLang("gui.basic_machines.title"));
        setParentGUI("main");
    }

    @Override
    protected void buildGUI(Player player) {
        // 清空并重新构建页面项目
        clearPageItems();

        // 添加基础机器按钮
        addPageItem(new EnhancedCraftingTableButton());
        addPageItem(new SmeltingFurnaceButton());
        addPageItem(new OreWasherButton());
        addPageItem(new OreSifterButton());
        addPageItem(new PressureMachineButton());

        // 调用父类方法构建GUI
        super.buildGUI(player);
    }

    @Override
    protected void buildExtraComponents(Player player) {
        // 添加返回按钮
        setComponent(45, new NavigationComponent(NavigationComponent.NavigationType.BACK, getParentGUIId()));
    }
}