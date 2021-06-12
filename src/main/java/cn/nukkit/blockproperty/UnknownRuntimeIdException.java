package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class UnknownRuntimeIdException extends IllegalStateException {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnknownRuntimeIdException() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnknownRuntimeIdException(String s) {
        super(s);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnknownRuntimeIdException(String message, Throwable cause) {
        super(message, cause);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnknownRuntimeIdException(Throwable cause) {
        super(cause);
    }
}
