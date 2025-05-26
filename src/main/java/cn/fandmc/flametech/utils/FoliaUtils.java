package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.Main;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Folia服务器兼容性工具类 - 使用FoliaLib 0.4.4
 */
public final class FoliaUtils {

    private static FoliaLib foliaLib;
    private static boolean initialized = false;

    /**
     * 初始化FoliaLib
     */
    public static void initialize(Main plugin) {
        if (!initialized) {
            foliaLib = new FoliaLib(plugin);
            initialized = true;
        }
    }

    /**
     * 确保FoliaLib已初始化
     */
    private static void ensureInitialized() {
        if (!initialized || foliaLib == null) {
            throw new IllegalStateException("FoliaUtils not initialized! Call initialize() first.");
        }
    }

    /**
     * 检查是否为Folia服务器
     */
    public static boolean isFoliaServer() {
        ensureInitialized();
        return foliaLib.isFolia();
    }

    /**
     * 检查是否为Paper服务器
     */
    public static boolean isPaperServer() {
        ensureInitialized();
        return foliaLib.isPaper();
    }

    /**
     * 检查是否为Spigot服务器
     */
    public static boolean isSpigotServer() {
        ensureInitialized();
        return foliaLib.isSpigot();
    }

    /**
     * 在正确的线程上下文中运行任务
     * @param location 任务相关的位置（用于Folia区域调度）
     * @param task 要执行的任务
     */
    public static void runTask(Location location, Runnable task) {
        ensureInitialized();
        foliaLib.getScheduler().runAtLocation(location, wrappedTask -> task.run());
    }

    /**
     * 在实体所在区域运行任务
     * @param entity 实体
     * @param task 要执行的任务
     */
    public static void runTask(Entity entity, Runnable task) {
        ensureInitialized();
        foliaLib.getScheduler().runAtEntity(entity, wrappedTask -> task.run());
    }

    /**
     * 运行全局任务（仅用于特定操作，如世界时间、天气等）
     * @param task 要执行的任务
     */
    public static void runGlobalTask(Runnable task) {
        ensureInitialized();
        foliaLib.getScheduler().runNextTick(wrappedTask -> task.run());
    }

    /**
     * 延迟运行任务
     * @param location 任务相关的位置
     * @param task 要执行的任务
     * @param delay 延迟时间（tick）
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runTaskLater(Location location, Runnable task, long delay) {
        ensureInitialized();
        return foliaLib.getScheduler().runAtLocationLater(location, task, delay);
    }

    /**
     * 在实体上延迟运行任务
     * @param entity 实体
     * @param task 要执行的任务
     * @param delay 延迟时间（tick）
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runTaskLater(Entity entity, Runnable task, long delay) {
        ensureInitialized();
        return foliaLib.getScheduler().runAtEntityLater(entity, task, delay);
    }

    /**
     * 延迟运行全局任务
     * @param task 要执行的任务
     * @param delay 延迟时间（tick）
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runGlobalTaskLater(Runnable task, long delay) {
        ensureInitialized();
        return foliaLib.getScheduler().runLater(task, delay);
    }

    /**
     * 运行重复任务
     * @param location 任务相关的位置
     * @param task 要执行的任务
     * @param delay 初始延迟（tick）
     * @param period 重复周期（tick）
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runTaskTimer(Location location, Runnable task, long delay, long period) {
        ensureInitialized();
        return foliaLib.getScheduler().runAtLocationTimer(location, task, delay, period);
    }

    /**
     * 在实体上运行重复任务
     * @param entity 实体
     * @param task 要执行的任务
     * @param delay 初始延迟（tick）
     * @param period 重复周期（tick）
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runTaskTimer(Entity entity, Runnable task, long delay, long period) {
        ensureInitialized();
        return foliaLib.getScheduler().runAtEntityTimer(entity, task, delay, period);
    }

    /**
     * 运行全局重复任务
     * @param task 要执行的任务
     * @param delay 初始延迟（tick）
     * @param period 重复周期（tick）
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runGlobalTaskTimer(Runnable task, long delay, long period) {
        ensureInitialized();
        return foliaLib.getScheduler().runTimer(task, delay, period);
    }

    /**
     * 运行异步任务
     * @param task 要执行的任务
     */
    public static void runAsync(Runnable task) {
        ensureInitialized();
        foliaLib.getScheduler().runAsync(wrappedTask -> task.run());
    }

    /**
     * 延迟运行异步任务
     * @param task 要执行的任务
     * @param delay 延迟时间（tick）
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runAsyncLater(Runnable task, long delay) {
        ensureInitialized();
        return foliaLib.getScheduler().runLaterAsync(task, delay);
    }

    /**
     * 运行重复异步任务
     * @param task 要执行的任务
     * @param delay 初始延迟（tick）
     * @param period 重复周期（tick）
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runAsyncTimer(Runnable task, long delay, long period) {
        ensureInitialized();
        return foliaLib.getScheduler().runTimerAsync(task, delay, period);
    }

    /**
     * 使用TimeUnit运行重复异步任务
     * @param task 要执行的任务
     * @param delay 初始延迟
     * @param period 重复周期
     * @param timeUnit 时间单位
     * @return WrappedTask 对象，可用于取消任务
     */
    public static WrappedTask runAsyncTimer(Runnable task, long delay, long period, TimeUnit timeUnit) {
        ensureInitialized();
        return foliaLib.getScheduler().runTimerAsync(task, delay, period, timeUnit);
    }

    /**
     * 异步传送实体
     * @param entity 要传送的实体
     * @param location 目标位置
     * @return CompletableFuture<Boolean> 表示传送是否成功
     */
    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
        ensureInitialized();
        return foliaLib.getScheduler().teleportAsync(entity, location);
    }

    /**
     * 异步传送实体（带传送原因）
     * @param entity 要传送的实体
     * @param location 目标位置
     * @param cause 传送原因
     * @return CompletableFuture<Boolean> 表示传送是否成功
     */
    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, PlayerTeleportEvent.TeleportCause cause) {
        ensureInitialized();
        return foliaLib.getScheduler().teleportAsync(entity, location, cause);
    }

    /**
     * 取消所有任务（应在插件onDisable时调用）
     */
    public static void cancelAllTasks() {
        ensureInitialized();
        foliaLib.getScheduler().cancelAllTasks();
    }

    /**
     * 取消特定任务
     * @param task 要取消的任务
     */
    public static void cancelTask(WrappedTask task) {
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * 检查任务是否已取消
     * @param task 要检查的任务
     * @return 如果任务已取消返回true
     */
    public static boolean isTaskCancelled(WrappedTask task) {
        return task != null && task.isCancelled();
    }

    /**
     * 获取调度器类型描述
     */
    public static String getSchedulerInfo() {
        ensureInitialized();
        if (foliaLib.isFolia()) {
            return "Folia (Region-based scheduling via FoliaLib)";
        } else if (foliaLib.isPaper()) {
            return "Paper (Traditional scheduling with async teleport support via FoliaLib)";
        } else {
            return "Spigot (Traditional scheduling via FoliaLib)";
        }
    }

    /**
     * 获取FoliaLib实例（供高级用法使用）
     */
    public static FoliaLib getFoliaLib() {
        ensureInitialized();
        return foliaLib;
    }

    private FoliaUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}