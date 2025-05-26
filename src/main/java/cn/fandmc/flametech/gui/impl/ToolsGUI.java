package cn.fandmc.flametech.gui.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.PaginatedGUI;
import cn.fandmc.flametech.gui.buttons.tools.ExplosivePickaxeButton;
import cn.fandmc.flametech.gui.buttons.tools.SmeltingPickaxeButton;
import cn.fandmc.flametech.gui.components.NavigationComponent;
import org.bukkit.entity.Player;

/**
 * 工具GUI
 */
public class ToolsGUI extends PaginatedGUI {

    public ToolsGUI(Main plugin) {
        super(plugin, "tools",
                plugin.getConfigManager().getLang("gui.tools.title"));
        setParentGUI("main");
    }

    @Override
    protected void buildGUI(Player player) {
        // 清空并重新构建页面项目
        clearPageItems();

        // 添加工具按钮
        addPageItem(new ExplosivePickaxeButton());
        addPageItem(new SmeltingPickaxeButton());

        // 调用父类方法构建GUI
        super.buildGUI(player);
    }

    @Override
    protected void buildExtraComponents(Player player) {
        // 添加返回按钮
        setComponent(45, new NavigationComponent(NavigationComponent.NavigationType.BACK, getParentGUIId()));
    }
}