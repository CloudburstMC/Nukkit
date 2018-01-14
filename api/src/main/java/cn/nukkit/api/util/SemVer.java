package cn.nukkit.api.util;

import lombok.Value;

@Value
public final class SemVer {
    private int major;
    private int minor;
    private int patch;

    public static SemVer fromString(String ver) {
        String[] parts = ver.split("\\.");
        if (parts.length < 3) {
            throw new IllegalArgumentException("3 version numbers required");
        }

        return new SemVer(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }

    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public boolean isCompatiblePatch(SemVer ver) {
        return Integer.compare(ver.patch, patch) >= 0 && isCompatibleMinor(ver);
    }

    public boolean isCompatibleMinor(SemVer ver) {
        return Integer.compare(ver.minor, minor) >= 0 && isCompatibleMajor(ver);
    }

    public boolean isCompatibleMajor(SemVer ver) {
        return Integer.compare(ver.major, major) >= 0;
    }
}
