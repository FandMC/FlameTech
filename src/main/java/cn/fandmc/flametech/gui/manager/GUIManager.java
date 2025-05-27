package cn.fandmc.flametech.gui.manager;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.gui.base.BaseGUI;
import cn.fandmc.flametech.gui.impl.*;
import cn.fandmc.flametech.gui.listeners.GUIListener;
import cn.fandmc.flametech.utils.MessageUtils;
import cn.fandmc.flametech.utils.ValidationUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GUI管理器 - 管理所有GUI界面
 */
public class GUIManager {

    private final Main plugin;
    private final Map<String, BaseGUI> registeredGUIs = new HashMap<>();
    private final Map<Player, BaseGUI> openGUIs = new HashMap<>();

    private final Set<Player> closingGUIs = ConcurrentHashMap.newKeySet();

    private static GUIManager instance;

    public GUIManager(Main plugin) {
        this.plugin = plugin;
        instance = this;

        // 注册GUI监听器
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    /**
     * 注册默认GUI
     */
    public void registerDefaultGUIs() {
        try {
            registerGUI(new MainGUI(plugin));

            registerGUI(new BasicMachinesGUI(plugin));
            registerGUI(new ToolsGUI(plugin));

            MessageUtils.logInfo("成功注册了 " + registeredGUIs.size() + " 个GUI");

        } catch (Exception e) {
            MessageUtils.logError("注册默认GUI失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 注册GUI
     */
    public void registerGUI(BaseGUI gui) {
        if (gui == null) {
            MessageUtils.logWarning("尝试注册空GUI");
            return;
        }

        String guiId = gui.getGuiId();
        if (registeredGUIs.containsKey(guiId)) {
            MessageUtils.logWarning("ID为 '" + guiId + "' 的GUI已存在");
            return;
        }

        registeredGUIs.put(guiId, gui);
        //MessageUtils.logInfo("注册GUI: " + guiId);
    }

    /**
     * 取消注册GUI
     */
    public void unregisterGUI(String guiId) {
        BaseGUI removed = registeredGUIs.remove(guiId);
        if (removed != null) {
            MessageUtils.logInfo("取消注册GUI: " + guiId);
        }
    }

    /**
     * 获取GUI
     */
    public Optional<BaseGUI> getGUI(String guiId) {
        return Optional.ofNullable(registeredGUIs.get(guiId));
    }

    /**
     * 打开GUI给玩家
     */
    public boolean openGUI(Player player, String guiId) {
        if (!ValidationUtils.isValidPlayer(player)) {
            MessageUtils.logWarning("Attempted to open GUI for invalid player");
            return false;
        }

        Optional<BaseGUI> guiOpt = getGUI(guiId);
        if (guiOpt.isEmpty()) {
            MessageUtils.sendMessage(player, "&c未找到GUI: " + guiId);
            MessageUtils.logWarning("Attempted to open non-existent GUI: " + guiId);
            return false;
        }

        try {
            BaseGUI gui = guiOpt.get();

            // 关闭当前打开的GUI（但不强制关闭库存）
            closeGUI(player, false);

            // 打开新GUI
            gui.open(player);
            openGUIs.put(player, gui);

            return true;

        } catch (Exception e) {
            MessageUtils.logError("Failed to open GUI " + guiId + " for player " + player.getName() + ": " + e.getMessage());
            MessageUtils.sendMessage(player, "&c打开界面时发生错误，请联系管理员");
            return false;
        }
    }

    /**
     * 关闭玩家的GUI（由事件监听器调用）
     */
    public void closeGUI(Player player) {
        closeGUI(player, false);
    }

    /**
     * 关闭玩家的GUI
     * @param player 玩家
     * @param forceCloseInventory 是否强制关闭库存（仅在必要时使用）
     */
    public void closeGUI(Player player, boolean forceCloseInventory) {
        if (!ValidationUtils.isValidPlayer(player)) {
            return;
        }

        // 防止递归调用
        if (closingGUIs.contains(player)) {
            return;
        }

        try {
            closingGUIs.add(player);

            BaseGUI currentGUI = openGUIs.remove(player);
            if (currentGUI != null) {
                try {
                    // 调用BaseGUI的关闭方法
                    currentGUI.handleClose(player);
                } catch (Exception e) {
                    MessageUtils.logError("Error during GUI close for player " + player.getName() + ": " + e.getMessage());
                }
            }

            // 只有在强制关闭时才主动关闭库存
            // 正常情况下，库存关闭事件会触发此方法，所以不需要再次关闭
            if (forceCloseInventory) {
                // 检查当前打开的库存是否是GUI
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof BaseGUI) {
                    player.closeInventory();
                }
            }

        } finally {
            // 确保从关闭集合中移除
            closingGUIs.remove(player);
        }
    }

    /**
     * 获取玩家当前打开的GUI
     */
    public Optional<BaseGUI> getOpenGUI(Player player) {
        return Optional.ofNullable(openGUIs.get(player));
    }

    /**
     * 检查玩家是否有打开的GUI
     */
    public boolean hasOpenGUI(Player player) {
        return openGUIs.containsKey(player);
    }

    /**
     * 刷新玩家的GUI
     */
    public void refreshGUI(Player player) {
        getOpenGUI(player).ifPresent(BaseGUI::refresh);
    }

    /**
     * 静态方法 - 向后兼容（修改方法名避免冲突）
     */
    public static boolean openGUIStatic(Player player, String guiId) {
        return instance != null && instance.openGUI(player, guiId);
    }

    /**
     * 静态方法 - 获取实例
     */
    public static GUIManager getInstance() {
        return instance;
    }

    /**
     * 获取所有已注册的GUI
     */
    public Map<String, BaseGUI> getAllGUIs() {
        return new HashMap<>(registeredGUIs);
    }

    /**
     * 获取已注册GUI数量
     */
    public int getRegisteredGUICount() {
        return registeredGUIs.size();
    }

    /**
     * 获取当前打开GUI的玩家数量
     */
    public int getOpenGUICount() {
        return openGUIs.size();
    }

    /**
     * 清空所有注册的GUI
     */
    public void clearAllGUIs() {
        // 先关闭所有打开的GUI
        for (Player player : new HashMap<>(openGUIs).keySet()) {
            closeGUI(player, true); // 强制关闭
        }

        registeredGUIs.clear();
        MessageUtils.logDebug("Cleared all registered GUIs");
    }

    /**
     * 关闭所有打开的GUI
     */
    public void closeAllGUIs() {
        for (Player player : new HashMap<>(openGUIs).keySet()) {
            closeGUI(player, true); // 强制关闭
        }
        MessageUtils.logDebug("Closed all open GUIs");
    }

    /**
     * 重新加载GUI管理器
     */
    public void reload() {
        closeAllGUIs();
        clearAllGUIs();
        registerDefaultGUIs();
        MessageUtils.logDebug("Reloaded GUI manager");
    }

    /**
     * 处理玩家断开连接
     */
    public void handlePlayerDisconnect(Player player) {
        openGUIs.remove(player);
        closingGUIs.remove(player); // 清理关闭状态
    }
}