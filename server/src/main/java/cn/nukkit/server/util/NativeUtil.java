/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.util;

import cn.nukkit.api.util.SemVer;
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
