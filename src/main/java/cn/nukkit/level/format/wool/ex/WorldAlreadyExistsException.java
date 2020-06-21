package cn.nukkit.level.format.wool.ex;

/**
 * Exception thrown when a world
 * already exists inside a data source.
 */
public class WorldAlreadyExistsException extends SlimeException {

    public WorldAlreadyExistsException(final String world) {
        super("World " + world + " already exists!");
    }

}