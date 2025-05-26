package cn.fandmc.flametech.gui.components;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.items.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 导航组件 - 用于GUI间导航
 */
public class NavigationComponent implements GUIComponent {

    public enum NavigationType {
        BACK, NEXT_PAGE, PREVIOUS_PAGE, CLOSE, OPEN_GUI
    }

    private final NavigationType type;
    private final String targetGUI;
    private final ItemStack displayItem;

    public NavigationComponent(NavigationType type) {
        this(type, null);
    }

    public NavigationComponent(NavigationType type, String targetGUI) {
        this.type = type;
        this.targetGUI = targetGUI;
        this.displayItem = createDisplayItem();
    }

    private ItemStack createDisplayItem() {
        switch (type) {
            case BACK:
                return ItemBuilder.createBackButton();
            case NEXT_PAGE:
                return ItemBuilder.createNextPageButton();
            case PREVIOUS_PAGE:
                return ItemBuilder.createPreviousPageButton();
            case CLOSE:
                return new ItemBuilder(Material.BARRIER)
                        .displayName("&c关闭")
                        .build();
            case OPEN_GUI:
                return new ItemBuilder(Material.COMPASS)
                        .displayName("&e打开界面")
                        .build();
            default:
                return new ItemBuilder(Material.STONE).build();
        }
    }

    @Override
    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        switch (type) {
            case BACK:
                handleBackButton(player);
                break;
            case CLOSE:
                // 直接关闭，让事件监听器处理后续逻辑
                player.closeInventory();
                break;
            case OPEN_GUI:
                if (targetGUI != null) {
                    Main.getInstance().getGuiManager().openGUI(player, targetGUI);
                }
                break;
            default:
                // 其他类型的导航由具体的GUI处理
                break;
        }
    }

    /**
     * 处理返回按钮逻辑
     */
    private void handleBackButton(Player player) {
        try {
            // 获取当前打开的GUI
            var currentGUI = Main.getInstance().getGuiManager().getOpenGUI(player);

            if (currentGUI.isPresent()) {
                String parentGUIId = currentGUI.get().getParentGUIId();

                if (parentGUIId != null && !parentGUIId.isEmpty()) {
                    // 打开父GUI
                    Main.getInstance().getGuiManager().openGUI(player, parentGUIId);
                } else {
                    // 没有父GUI，直接关闭
                    player.closeInventory();
                }
            } else {
                // 找不到当前GUI，直接关闭
                player.closeInventory();
            }
        } catch (Exception e) {
            // 出现错误时直接关闭
            player.closeInventory();
        }
    }
}