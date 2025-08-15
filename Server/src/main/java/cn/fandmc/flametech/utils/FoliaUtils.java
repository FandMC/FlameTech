package cn.fandmc.flametech.utils;

import cn.fandmc.flametech.Main;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Folia兼容性工具类
 * 提供统一的任务调度接口，兼容Folia和Paper/Spigot
 * 直接使用Folia原生API而不依赖第三方库
 */
public final class FoliaUtils {

    private static Main plugin;
    private static boolean initialized = false;
    private static boolean isFolia;

    /**
     * 初始化FoliaUtils
     */
    public static void initialize(Main pluginInstance) {
        if (!initialized) {
            plugin = pluginInstance;
            // 检测是否为Folia环境
            try {
                Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
                isFolia = true;
            } catch (ClassNotFoundException e) {
                isFolia = false;
            }
            initialized = true;
        }
    }

    /**
     * 确保已初始化
     */
    private static void ensureInitialized() {
        if (!initialized || plugin == null) {
            throw new IllegalStateException("FoliaUtils not initialized! Call initialize() first.");
        }
    }

    /**
     * 检查是否为Folia环境
     */
    public static boolean isFolia() {
        return isFolia;
    }

    /**
     * 在正确的线程上下文中运行任务
     * @param location 任务相关的位置（用于Folia区域调度）
     * @param task 要执行的任务
     */
    public static Object runTask(Location location, Runnable task) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> task.run());
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * 在实体所在区域运行任务
     * @param entity 实体
     * @param task 要执行的任务
     */
    public static Object runTask(Entity entity, Runnable task) {
        ensureInitialized();
        if (isFolia) {
            return entity.getScheduler().run(plugin, scheduledTask -> task.run(), null);
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * 运行全局任务（仅用于特定操作，如世界时间、天气等）
     * @param task 要执行的任务
     */
    public static Object runGlobalTask(Runnable task) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * 延迟运行任务
     * @param location 任务相关的位置
     * @param task 要执行的任务
     * @param delay 延迟时间（tick）
     * @return 任务对象，可用于取消任务
     */
    public static Object runTaskLater(Location location, Runnable task, long delay) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getRegionScheduler().runDelayed(plugin, location, scheduledTask -> task.run(), delay);
        } else {
            return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    /**
     * 在实体上延迟运行任务
     * @param entity 实体
     * @param task 要执行的任务
     * @param delay 延迟时间（tick）
     * @return 任务对象，可用于取消任务
     */
    public static Object runTaskLater(Entity entity, Runnable task, long delay) {
        ensureInitialized();
        if (isFolia) {
            return entity.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delay);
        } else {
            return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    /**
     * 延迟运行全局任务
     * @param task 要执行的任务
     * @param delay 延迟时间（tick）
     * @return 任务对象，可用于取消任务
     */
    public static Object runGlobalTaskLater(Runnable task, long delay) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), delay);
        } else {
            return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    /**
     * 运行重复任务
     * @param location 任务相关的位置
     * @param task 要执行的任务
     * @param delay 初始延迟（tick）
     * @param period 重复周期（tick）
     * @return 任务对象，可用于取消任务
     */
    public static Object runTaskTimer(Location location, Runnable task, long delay, long period) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, scheduledTask -> task.run(), delay, period);
        } else {
            return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    /**
     * 在实体上运行重复任务
     * @param entity 实体
     * @param task 要执行的任务
     * @param delay 初始延迟（tick）
     * @param period 重复周期（tick）
     * @return 任务对象，可用于取消任务
     */
    public static Object runTaskTimer(Entity entity, Runnable task, long delay, long period) {
        ensureInitialized();
        if (isFolia) {
            return entity.getScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), null, delay, period);
        } else {
            return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    /**
     * 运行全局重复任务
     * @param task 要执行的任务
     * @param delay 初始延迟（tick）
     * @param period 重复周期（tick）
     * @return 任务对象，可用于取消任务
     */
    public static Object runGlobalTaskTimer(Runnable task, long delay, long period) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), delay, period);
        } else {
            return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    /**
     * 运行异步任务
     * @param task 要执行的任务
     */
    public static Object runAsync(Runnable task) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
        } else {
            return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * 延迟运行异步任务
     * @param task 要执行的任务
     * @param delay 延迟时间（tick）
     * @return 任务对象，可用于取消任务
     */
    public static Object runAsyncLater(Runnable task, long delay) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> task.run(), delay * 50, TimeUnit.MILLISECONDS);
        } else {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
        }
    }

    /**
     * 运行重复异步任务
     * @param task 要执行的任务
     * @param delay 初始延迟（tick）
     * @param period 重复周期（tick）
     * @return 任务对象，可用于取消任务
     */
    public static Object runAsyncTimer(Runnable task, long delay, long period) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getAsyncScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), delay * 50, period * 50, TimeUnit.MILLISECONDS);
        } else {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
        }
    }

    /**
     * 使用TimeUnit运行重复异步任务
     * @param task 要执行的任务
     * @param delay 初始延迟
     * @param period 重复周期
     * @param timeUnit 时间单位
     * @return 任务对象，可用于取消任务
     */
    public static Object runAsyncTimer(Runnable task, long delay, long period, TimeUnit timeUnit) {
        ensureInitialized();
        if (isFolia) {
            return Bukkit.getAsyncScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), delay, period, timeUnit);
        } else {
            // 转换为tick
            long delayTicks = timeUnit.toMillis(delay) / 50;
            long periodTicks = timeUnit.toMillis(period) / 50;
            return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
        }
    }

    /**
     * 异步传送实体
     * @param entity 要传送的实体
     * @param location 目标位置
     * @return CompletableFuture<Boolean> 表示传送是否成功
     */
    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
        ensureInitialized();
        if (isFolia) {
            return entity.teleportAsync(location);
        } else {
            // Paper/Spigot环境下也支持异步传送
            return entity.teleportAsync(location);
        }
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
        if (isFolia) {
            return entity.teleportAsync(location, cause);
        } else {
            return entity.teleportAsync(location, cause);
        }
    }

    /**
     * 取消所有任务（应在插件onDisable时调用）
     */
    public static void cancelAllTasks() {
        ensureInitialized();
        if (isFolia) {
            // Folia环境下，任务会在插件禁用时自动取消
            // 这里可以添加额外的清理逻辑
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }

    /**
     * 取消特定任务
     * @param task 要取消的任务
     */
    public static void cancelTask(Object task) {
        if (task == null) return;
        
        if (isFolia) {
            if (task instanceof ScheduledTask) {
                ((ScheduledTask) task).cancel();
            }
        } else {
            if (task instanceof BukkitTask) {
                ((BukkitTask) task).cancel();
            }
        }
    }

    /**
     * 检查任务是否已取消
     * @param task 要检查的任务
     * @return 如果任务已取消返回true
     */
    public static boolean isTaskCancelled(Object task) {
        if (task == null) return true;
        
        if (isFolia) {
            if (task instanceof ScheduledTask) {
                return ((ScheduledTask) task).isCancelled();
            }
        } else {
            if (task instanceof BukkitTask) {
                return ((BukkitTask) task).isCancelled();
            }
        }
        return true;
    }

    /**
     * 获取调度器类型描述
     */
    public static String getSchedulerInfo() {
        ensureInitialized();
        if (isFolia) {
            return "Folia (Region-based scheduling using native API)";
        } else {
            return "Paper/Spigot (Traditional scheduling using Bukkit API)";
        }
    }

    /**
     * 获取插件实例（供高级用法使用）
     */
    public static Main getPlugin() {
        ensureInitialized();
        return plugin;
    }

    private FoliaUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}