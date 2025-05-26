package cn.fandmc.flametech.exceptions;

/**
 * FlameTech插件基础异常类
 */
public class FlameTechException extends Exception {

    private final String errorCode;

    public FlameTechException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
    }

    public FlameTechException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FlameTechException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
    }

    public FlameTechException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("FlameTechException[%s]: %s", errorCode, getMessage());
    }
}