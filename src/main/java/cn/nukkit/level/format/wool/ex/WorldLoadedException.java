package cn.nukkit.level.format.wool.ex;

/**
 * Exception thrown when a world is loaded
 * when trying to import it.
 */
public class WorldLoadedException extends SlimeException {

    public WorldLoadedException(final String worldName) {
        super("World " + worldName + " is loaded! Unload it before importing it.");
    }

}