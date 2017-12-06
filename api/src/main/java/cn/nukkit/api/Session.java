package cn.nukkit.api;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public interface Session {
    @Nonnull
    String getName();

    @Nonnull
    Optional<InetSocketAddress> getRemoteAddress();

    @Nonnull
    UUID getUniqueId();

    boolean isXboxAuthenticated();

    @Nonnull
    Optional<String> getXuid();
}
