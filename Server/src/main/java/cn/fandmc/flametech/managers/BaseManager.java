package cn.fandmc.flametech.managers;

import cn.fandmc.flametech.Main;
import cn.fandmc.flametech.utils.MessageUtils;

/**
 * 管理器基类 - 提供通用的管理器功能
 * @param <T> 管理的对象类型
 */
public abstract class BaseManager<T> {

    protected final Main plugin;
    protected final String managerName;

    /**
     * 构造函数
     * @param plugin 插件实例
     * @param managerName 管理器名称（用于日志）
     */
    protected BaseManager(Main plugin, String managerName) {
        this.plugin = plugin;
        this.managerName = managerName;
    }

    /**
     * 注册默认内容 - 子类必须实现
     */
    public abstract void registerDefaults();

    /**
     * 清空所有内容 - 子类必须实现
     */
    public abstract void clearAll();

    /**
     * 获取已注册数量 - 子类必须实现
     */
    public abstract int getRegisteredCount();

    /**
     * 获取管理器名称
     */
    public String getManagerName() {
        return managerName;
    }

    /**
     * 重载管理器 - 通用实现
     */
    public void reload() {
        try {
            MessageUtils.logInfo("重载" + managerName + "...");
            
            // 清空现有内容
            clearAll();
            
            // 重新注册默认内容
            registerDefaults();
            
            MessageUtils.logInfo(managerName + "重载完成，共注册 " + getRegisteredCount() + " 项");
            
        } catch (Exception e) {
            MessageUtils.logError("重载" + managerName + "失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 记录注册成功的日志
     */
    protected void logRegistrationSuccess() {
        MessageUtils.logInfo("成功注册了 " + getRegisteredCount() + " 个" + managerName.replace("管理器", ""));
    }

    /**
     * 记录注册失败的日志
     */
    protected void logRegistrationFailure(Exception e) {
        MessageUtils.logError("注册默认" + managerName.replace("管理器", "") + "失败: " + e.getMessage());
    }

    /**
     * 记录清空的调试日志
     */
    protected void logClearDebug() {
        MessageUtils.logDebug("清空所有" + managerName.replace("管理器", ""));
    }
}