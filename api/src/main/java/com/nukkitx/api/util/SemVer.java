package com.nukkitx.api.util;

import com.google.common.base.Preconditions;
import lombok.Value;

import javax.annotation.Nonnull;
import java.util.Objects;

@Value
public final class SemVer {
    private int major;
    private int minor;
    private int patch;

    public static SemVer fromString(@Nonnull String ver) throws NumberFormatException {
        Preconditions.checkNotNull(ver, "ver");
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

    /**
     * Checks whether the version is compatible with minor and major.
     *
     * @param ver version to check against
     * @return compatible
     */
    public boolean isCompatibleMinor(SemVer ver) {
        return isCompatibleMajor(ver) && ver.minor >= minor;
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

    /**
     * Checks whether the version is compatible with patch, minor, major.
     *
     * @param ver version to check against
     * @return compatible
     */
    public boolean isCompatible(SemVer ver) {
        return isCompatibleMinor(ver) && patch >= ver.patch;
    }

    public boolean isNewerOrEqual(SemVer ver) {
        return major >= ver.major && minor >= ver.minor && patch >= ver.patch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemVer)) return false;
        SemVer that = (SemVer) o;

        return this.major == that.major && this.minor == that.minor && this.patch == that.patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + '.' + patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }
}
