package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelException extends ServerException {
    public LevelException(String message) {
        super(message);
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public LevelException(String message, Throwable cause) {
        super(message, cause);
    }
}
