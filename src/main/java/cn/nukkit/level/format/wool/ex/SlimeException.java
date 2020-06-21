package cn.nukkit.level.format.wool.ex;

/**
 * Generic SWM exception.
 */
public class SlimeException extends Exception {

    public SlimeException(final String message) {
        super(message);
    }

    public SlimeException(final String message, final Exception ex) {
        super(message, ex);
    }

}