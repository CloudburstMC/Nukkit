package cn.nukkit.level.format.wool.ex;

/**
 * Exception thrown when a world could not
 * be read from its data file.
 */
public class CorruptedWorldException extends SlimeException {

    public CorruptedWorldException(final String world) {
        this(world, null);
    }

    public CorruptedWorldException(final String world, final Exception ex) {
        super("World " + world + " seems to be corrupted", ex);
    }

}