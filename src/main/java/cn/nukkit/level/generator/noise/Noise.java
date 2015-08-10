package cn.nukkit.level.generator.noise;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Noise {
    protected int[] perm;
    protected double offsetX = 0;
    protected double offsetY = 0;
    protected double offsetZ = 0;
    protected double octaves = 8;
    protected double persistence;
    protected double expansion;

    public static int floor(double x) {
        return x >= 0 ? (int) x : (int) (x - 1);
    }

    public static double fade(double x) {
        return x * x * x * (x * (x * 6 - 15) + 10);
    }

    public static double lerp(double x, double y, double z) {
        return y + x * (z - y);
    }

    public static double linearLerp(double x, double x1, double x2, double q0, double q1) {
        return ((x2 - x) / (x2 - x1)) * q0 + ((x - x1) / (x2 - x1)) * q1;
    }

    public static double bilinearLerp(double x, double y, double q00, double q01, double q10, double q11, double x1, double x2, double y1, double y2) {
        double dx1 = ((x2 - x) / (x2 - x1));
        double dx2 = ((x - x1) / (x2 - x1));

        return ((y2 - y) / (y2 - y1)) * (
                dx1 * q00 + dx2 * q10
        ) + ((y - y1) / (y2 - y1)) * (
                dx1 * q01 + dx2 * q11
        );
    }

    public static double trilinearLerp(double x, double y, double z, double q000, double q001, double q010, double q011, double q100, double q101, double q110, double q111, double x1, double x2, double y1, double y2, double z1, double z2) {
        double dx1 = ((x2 - x) / (x2 - x1));
        double dx2 = ((x - x1) / (x2 - x1));
        double dy1 = ((y2 - y) / (y2 - y1));
        double dy2 = ((y - y1) / (y2 - y1));

        return ((z2 - z) / (z2 - z1)) * (
                dy1 * (
                        dx1 * q000 + dx2 * q100
                ) + dy2 * (
                        dx1 * q001 + dx2 * q101
                )
        ) + ((z - z1) / (z2 - z1)) * (
                dy1 * (
                        dx1 * q010 + dx2 * q110
                ) + dy2 * (
                        dx1 * q011 + dx2 * q111
                )
        );
    }

    public static double grad(int hash, double x, double y, double z) {
        hash &= 15;
        double u = hash < 8 ? x : y;
        double v = hash < 4 ? y : ((hash == 12 || hash == 14) ? x :
                z);

        return ((hash & 1) == 0 ? u : -u) + ((hash & 2) == 0 ? v : -v);
    }

    abstract public double getNoise2D(double x, double z);

    abstract public double getNoise3D(double x, double y, double z);

    public double noise2D(double x, double z) {
        return noise2D(x, z, false);
    }

    public double noise2D(double x, double z, boolean normalized) {
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

    public double noise3D(double x, double y, double z) {
        return noise3D(x, y, z, false);
    }

    public double noise3D(double x, double y, double z, boolean normalized) {
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

    public void setOffset(double x, double y, double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }
}
