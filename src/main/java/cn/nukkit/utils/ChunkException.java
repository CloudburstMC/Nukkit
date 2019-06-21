package cn.nukkit.utils;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkException extends RuntimeException {

    public ChunkException(String message) {
        super(message);
    }

    public ChunkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChunkException(Throwable cause) {
        super(cause);
    }

}
