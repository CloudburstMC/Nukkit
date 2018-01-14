package cn.nukkit.api;

import cn.nukkit.api.util.data.DeviceOS;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;

public interface Session extends Stateless {

    @Nonnull
    Optional<InetSocketAddress> getRemoteAddress();

    boolean isXboxAuthenticated();

    @Nonnull
    Optional<String> getXuid();

    @Nonnull
    DeviceOS getDeviceOS();

    boolean isEducationEdition();

    @Nonnull
    String getDeviceModel();

    @Nonnull
    String getGameVersion();

    @Nonnull
    String getServerAddress();

    @Nonnull
    Optional<String> getActiveDirectoryRole();
}
