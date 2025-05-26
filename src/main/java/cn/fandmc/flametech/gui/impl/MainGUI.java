package cn.fandmc.flametech.gui.impl;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.PaginatedGUI;
import cn.fandmc.flametech.gui.buttons.main.BasicMachinesButton;
import cn.fandmc.flametech.gui.buttons.main.ToolsButton;
import org.bukkit.entity.Player;

/**
 * 主界面GUI
 */
public class MainGUI extends PaginatedGUI {

    public MainGUI(Main plugin) {
        super(plugin, "main", plugin.getConfigManager().getLang("guide_book.display_name"));
    }

    @Override
    protected void buildGUI(Player player) {
        // 清空并重新构建页面项目
        clearPageItems();

        // 添加主要功能按钮
        addPageItem(new BasicMachinesButton());
        addPageItem(new ToolsButton());

        // 调用父类方法构建GUI
        super.buildGUI(player);
    }

    @Override
    protected void buildExtraComponents(Player player) {
        // 主界面不需要返回按钮
    }
}