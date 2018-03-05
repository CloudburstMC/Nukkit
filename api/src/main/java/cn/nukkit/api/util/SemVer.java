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
        return Integer.compare(ver.major, major) >= 0;
    }
}
