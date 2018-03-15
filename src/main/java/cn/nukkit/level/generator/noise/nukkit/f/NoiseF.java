package cn.nukkit.level.generator.noise.nukkit.f;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class NoiseF {
    protected int[] perm;
    protected float offsetX = 0;
    protected float offsetY = 0;
    protected float offsetZ = 0;
    protected float octaves = 8;
    protected float persistence;
    protected float expansion;

    public static int floor(float x) {
        return x >= 0 ? (int) x : (int) (x - 1);
    }

    public static float fade(float x) {
        return x * x * x * (x * (x * 6 - 15) + 10);
    }

    public static float lerp(float x, float y, float z) {
        return y + x * (z - y);
    }

    public static float linearLerp(float x, float x1, float x2, float q0, float q1) {
        return ((x2 - x) / (x2 - x1)) * q0 + ((x - x1) / (x2 - x1)) * q1;
    }

    public static float bilinearLerp(float x, float y, float q00, float q01, float q10, float q11, float x1, float x2, float y1, float y2) {
        float dx1 = ((x2 - x) / (x2 - x1));
        float dx2 = ((x - x1) / (x2 - x1));

        return ((y2 - y) / (y2 - y1)) * (
                dx1 * q00 + dx2 * q10
        ) + ((y - y1) / (y2 - y1)) * (
                dx1 * q01 + dx2 * q11
        );
    }

    public static float trilinearLerp(float x, float y, float z, float q000, float q001, float q010, float q011, float q100, float q101, float q110, float q111, float x1, float x2, float y1, float y2, float z1, float z2) {
        float dx1 = ((x2 - x) / (x2 - x1));
        float dx2 = ((x - x1) / (x2 - x1));
        float dy1 = ((y2 - y) / (y2 - y1));
        float dy2 = ((y - y1) / (y2 - y1));

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

    public static float grad(int hash, float x, float y, float z) {
        hash &= 15;
        float u = hash < 8 ? x : y;
        float v = hash < 4 ? y : ((hash == 12 || hash == 14) ? x :
                z);

        return ((hash & 1) == 0 ? u : -u) + ((hash & 2) == 0 ? v : -v);
    }

    abstract public float getNoise2D(float x, float z);

    abstract public float getNoise3D(float x, float y, float z);

    public float noise2D(float x, float z) {
        return noise2D(x, z, false);
    }

    public float noise2D(float x, float z, boolean normalized) {
        float result = 0;
        float amp = 1;
        float freq = 1;
        float max = 0;

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

    public float noise3D(float x, float y, float z) {
        return noise3D(x, y, z, false);
    }

    public float noise3D(float x, float y, float z, boolean normalized) {
        float result = 0;
        float amp = 1;
        float freq = 1;
        float max = 0;

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

    public void setOffset(float x, float y, float z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }
}
