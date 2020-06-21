package cn.nukkit.level.generator.noise.nukkit.d;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class NoiseD {

    protected int[] perm;

    protected double offsetX = 0;

    protected double offsetY = 0;

    protected double offsetZ = 0;

    protected double octaves = 8;

    protected double persistence;

    protected double expansion;

    public static int floor(final double x) {
        return x >= 0 ? (int) x : (int) (x - 1);
    }

    public static double fade(final double x) {
        return x * x * x * (x * (x * 6 - 15) + 10);
    }

    public static double lerp(final double x, final double y, final double z) {
        return y + x * (z - y);
    }

    public static double linearLerp(final double x, final double x1, final double x2, final double q0, final double q1) {
        return (x2 - x) / (x2 - x1) * q0 + (x - x1) / (x2 - x1) * q1;
    }

    public static double bilinearLerp(final double x, final double y, final double q00, final double q01, final double q10, final double q11, final double x1, final double x2, final double y1, final double y2) {
        final double dx1 = (x2 - x) / (x2 - x1);
        final double dx2 = (x - x1) / (x2 - x1);

        return (y2 - y) / (y2 - y1) * (
            dx1 * q00 + dx2 * q10
        ) + (y - y1) / (y2 - y1) * (
            dx1 * q01 + dx2 * q11
        );
    }

    public static double trilinearLerp(final double x, final double y, final double z, final double q000, final double q001, final double q010, final double q011, final double q100, final double q101, final double q110, final double q111, final double x1, final double x2, final double y1, final double y2, final double z1, final double z2) {
        final double dx1 = (x2 - x) / (x2 - x1);
        final double dx2 = (x - x1) / (x2 - x1);
        final double dy1 = (y2 - y) / (y2 - y1);
        final double dy2 = (y - y1) / (y2 - y1);

        return (z2 - z) / (z2 - z1) * (
            dy1 * (
                dx1 * q000 + dx2 * q100
            ) + dy2 * (
                dx1 * q001 + dx2 * q101
            )
        ) + (z - z1) / (z2 - z1) * (
            dy1 * (
                dx1 * q010 + dx2 * q110
            ) + dy2 * (
                dx1 * q011 + dx2 * q111
            )
        );
    }

    public static double grad(int hash, final double x, final double y, final double z) {
        hash &= 15;
        final double u = hash < 8 ? x : y;
        final double v = hash < 4 ? y : hash == 12 || hash == 14 ? x :
            z;

        return ((hash & 1) == 0 ? u : -u) + ((hash & 2) == 0 ? v : -v);
    }

    public abstract double getNoise2D(double x, double z);

    public abstract double getNoise3D(double x, double y, double z);

    public double noise2D(final double x, final double z) {
        return this.noise2D(x, z, false);
    }

    public double noise2D(double x, double z, final boolean normalized) {
        double result = 0;
        double amp = 1;
        double freq = 1;
        double max = 0;

        x *= this.expansion;
        z *= this.expansion;

        for (int i = 0; i < this.octaves; ++i) {
            result += this.getNoise2D(x * freq, z * freq) * amp;
            max += amp;
            freq *= 2;
            amp *= this.persistence;
        }

        if (normalized) {
            result /= max;
        }

        return result;
    }

    public double noise3D(final double x, final double y, final double z) {
        return this.noise3D(x, y, z, false);
    }

    public double noise3D(double x, double y, double z, final boolean normalized) {
        double result = 0;
        double amp = 1;
        double freq = 1;
        double max = 0;

        x *= this.expansion;
        y *= this.expansion;
        z *= this.expansion;

        for (int i = 0; i < this.octaves; ++i) {
            result += this.getNoise3D(x * freq, y * freq, z * freq) * amp;
            max += amp;
            freq *= 2;
            amp *= this.persistence;
        }

        if (normalized) {
            result /= max;
        }

        return result;
    }

    public void setOffset(final double x, final double y, final double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }

}
