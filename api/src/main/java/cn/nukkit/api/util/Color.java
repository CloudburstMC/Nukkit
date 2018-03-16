package cn.nukkit.api.util;

import com.google.common.base.Preconditions;
import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public final class Color {
    private final byte r;
    private final byte g;
    private final byte b;

    private Color(int r, int b, int g) {
        Preconditions.checkArgument(r >= 0 && r <= 255, "r is not 0 - 255");
        Preconditions.checkArgument(g >= 0 && g <= 255, "g is not 0 - 255");
        Preconditions.checkArgument(b >= 0 && b <= 255, "b is not 0 - 255");
        this.r = (byte) r;
        this.b = (byte) b;
        this.g = (byte) g;
    }

    public static Color of(int r, int g, int b) {
        return new Color(r, g, b);
    }

    public int getR() {
        return (r & 0xff);
    }

    public int getB() {
        return (b & 0xff);
    }

    public int getG() {
        return (g & 0xff);
    }
}
