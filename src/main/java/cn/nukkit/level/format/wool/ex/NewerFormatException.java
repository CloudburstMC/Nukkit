package cn.nukkit.level.format.wool.ex;

/**
 * Exception thrown when a world is encoded
 * using a newer SRF format than the one that
 * SWM supports.
 */
public class NewerFormatException extends SlimeException {

    public NewerFormatException(final byte version) {
        super("v" + version);
    }

}