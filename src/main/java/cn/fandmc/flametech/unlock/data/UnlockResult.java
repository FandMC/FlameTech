package cn.fandmc.flametech.unlock.data;

/**
 * 解锁操作结果
 */
public class UnlockResult {

    public enum Status {
        SUCCESS,
        ALREADY_UNLOCKED,
        INSUFFICIENT_EXP,
        ITEM_NOT_FOUND,
        PLAYER_NOT_FOUND,
        ERROR
    }

    private final Status status;
    private final String message;
    private final int requiredExp;
    private final String itemId;

    public UnlockResult(Status status, String message) {
        this(status, message, 0, null);
    }

    public UnlockResult(Status status, String message, int requiredExp) {
        this(status, message, requiredExp, null);
    }

    public UnlockResult(Status status, String message, int requiredExp, String itemId) {
        this.status = status;
        this.message = message != null ? message : "";
        this.requiredExp = Math.max(0, requiredExp);
        this.itemId = itemId;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public int getRequiredExp() { return requiredExp; }
    public String getItemId() { return itemId; }

    // 静态工厂方法
    public static UnlockResult success(String itemId) {
        return new UnlockResult(Status.SUCCESS, "success", 0, itemId);
    }

    public static UnlockResult alreadyUnlocked(String itemId) {
        return new UnlockResult(Status.ALREADY_UNLOCKED, "already_unlocked", 0, itemId);
    }

    public static UnlockResult insufficientExp(int required, String itemId) {
        return new UnlockResult(Status.INSUFFICIENT_EXP, "insufficient_exp", required, itemId);
    }

    public static UnlockResult itemNotFound(String itemId) {
        return new UnlockResult(Status.ITEM_NOT_FOUND, "item_not_found", 0, itemId);
    }

    public static UnlockResult playerNotFound() {
        return new UnlockResult(Status.PLAYER_NOT_FOUND, "player_not_found");
    }

    public static UnlockResult error(String message) {
        return new UnlockResult(Status.ERROR, message);
    }

    @Override
    public String toString() {
        return String.format("UnlockResult{status=%s, message='%s', item='%s'}",
                status, message, itemId);
    }
}
