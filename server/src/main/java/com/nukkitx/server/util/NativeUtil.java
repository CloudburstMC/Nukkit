package com.nukkitx.server.util;

import com.nukkitx.api.util.SemVer;
import io.netty.channel.epoll.Native;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@UtilityClass
public final class NativeUtil {
    private static final Optional<SemVer> KERNEL_VERSION;
    private static final SemVer REUSEPORT_VERSION = new SemVer(3, 9, 0);
    private static final boolean REUSEPORT_AVAILABLE;

    static {
        String kernelVersion;
        try {
            kernelVersion = Native.KERNEL_VERSION;
        } catch (Throwable e) {
            kernelVersion = null;
        }
        if (kernelVersion != null && kernelVersion.contains("-")) {
            int index = kernelVersion.indexOf('-');
            if (index > -1) {
                kernelVersion = kernelVersion.substring(0, index);
            }
            SemVer kernalVer = SemVer.fromString(kernelVersion);
            KERNEL_VERSION = Optional.of(kernalVer);
            REUSEPORT_AVAILABLE = kernalVer.isNewerOrEqual(REUSEPORT_VERSION);
        } else {
            KERNEL_VERSION = Optional.empty();
            REUSEPORT_AVAILABLE = false;
        }
    }

    public static Optional<SemVer> getKernelVersion() {
        return KERNEL_VERSION;
    }

    public static boolean isReusePortAvailable() {
        return REUSEPORT_AVAILABLE;
    }
}
