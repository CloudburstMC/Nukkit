package cn.nukkit.level.format.wool.ex;

import java.io.File;

/**
 * Exception thrown when a folder does
 * not contain a valid Minecraft world.
 */
public class InvalidWorldException extends SlimeException {

    public InvalidWorldException(final File worldDir) {
        super("Directory " + worldDir.getPath() + " does not contain a valid MC world!");
    }

}