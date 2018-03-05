package cn.nukkit.api.level;

public class LevelException extends Exception {

    public LevelException() {
        super();
    }

    public LevelException(String message) {
        super(message);
    }

    public LevelException(Throwable cause) {
        super(cause);
    }

    public LevelException(String message, Throwable cause) {
        super(message, cause);
    }

    public LevelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
