package cn.nukkit.utils.functional;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.Objects;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public interface BooleanConsumer {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void accept(boolean value);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default BooleanConsumer andThen(BooleanConsumer after) {
        Objects.requireNonNull(after);
        return (boolean t) -> { accept(t); after.accept(t); };
    }
}
