package cn.nukkit.level.format.wool.ex;

/**
 * Exception thrown when a world is locked
 * and is being accessed on write-mode.
 */
public class WorldInUseException extends SlimeException {

    public WorldInUseException(final String world) {
        super(world);
    }

}