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

package cn.nukkit.api.util;

import lombok.Value;

@Value
public final class SemVer {
    private int major;
    private int minor;
    private int patch;

    public static SemVer fromString(String ver) {
        String[] parts = ver.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("At least 2 version numbers required");
        }

        return new SemVer(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                parts.length == 2 ? 0 : Integer.parseInt(parts[2])
        );
    }

    public String toString() {
        return major + "." + minor + '.' + patch;
    }

    /**
     * Checks whether the version is compatible with patch, minor, major.
     *
     * @param ver version to check against
     * @return compatible
     */
    public boolean isCompatiblePatch(SemVer ver) {
        return Integer.compare(ver.patch, patch) >= 0 && isCompatibleMinor(ver);
    }

    /**
     * Checks whether the version is compatible with minor and major.
     *
     * @param ver version to check against
     * @return compatible
     */
    public boolean isCompatibleMinor(SemVer ver) {
        return Integer.compare(ver.minor, minor) >= 0 && isCompatibleMajor(ver);
    }

    /**
     * Checks whether the version is compatible with major.
     *
     * @param ver version to check against
     * @return compatible
     */
    public boolean isCompatibleMajor(SemVer ver) {
        return ver.major == major;
    }

    public boolean isNewerOrEqual(SemVer ver) {
        return Integer.compare(major, ver.major) >= 0 &&
                Integer.compare(minor, ver.minor) >= 0 &&
                Integer.compare(patch, ver.patch) >= 0;
    }
}
