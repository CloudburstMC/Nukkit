package cn.nukkit.utils;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkException extends RuntimeException {
    public ChunkException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
