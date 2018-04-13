package cn.nukkit.api.util;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Color {
    private final byte r;
    private final byte g;
    private final byte b;

    private Color(int r, int b, int g) {
        Preconditions.checkArgument(r >= 0 && r < 256, "r is not 0 - 255");
        Preconditions.checkArgument(g >= 0 && g < 256, "g is not 0 - 255");
        Preconditions.checkArgument(b >= 0 && b < 256, "b is not 0 - 255");
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

    public int getG() {
        return (g & 0xff);
    }

    public int getB() {
        return (b & 0xff);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Color.class != o.getClass()) return false;
        Color that = (Color) o;
        return this.r == that.r && this.g == that.g && this.b == that.b;
    }

    @Override
    public int hashCode() {
        return 31 * r + 31 * g + 31 * b;
    }

    @Override
    public String toString() {
        return "Color(" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                ')';
    }
}
