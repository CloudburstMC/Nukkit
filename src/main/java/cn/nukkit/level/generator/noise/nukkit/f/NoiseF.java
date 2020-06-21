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

    public static int floor(final float x) {
        return x >= 0 ? (int) x : (int) (x - 1);
    }

    public static float fade(final float x) {
        return x * x * x * (x * (x * 6 - 15) + 10);
    }

    public static float lerp(final float x, final float y, final float z) {
        return y + x * (z - y);
    }

    public static float linearLerp(final float x, final float x1, final float x2, final float q0, final float q1) {
        return (x2 - x) / (x2 - x1) * q0 + (x - x1) / (x2 - x1) * q1;
    }

    public static float bilinearLerp(final float x, final float y, final float q00, final float q01, final float q10, final float q11, final float x1, final float x2, final float y1, final float y2) {
        final float dx1 = (x2 - x) / (x2 - x1);
        final float dx2 = (x - x1) / (x2 - x1);

        return (y2 - y) / (y2 - y1) * (
            dx1 * q00 + dx2 * q10
        ) + (y - y1) / (y2 - y1) * (
            dx1 * q01 + dx2 * q11
        );
    }

    public static float trilinearLerp(final float x, final float y, final float z, final float q000, final float q001, final float q010, final float q011, final float q100, final float q101, final float q110, final float q111, final float x1, final float x2, final float y1, final float y2, final float z1, final float z2) {
        final float dx1 = (x2 - x) / (x2 - x1);
        final float dx2 = (x - x1) / (x2 - x1);
        final float dy1 = (y2 - y) / (y2 - y1);
        final float dy2 = (y - y1) / (y2 - y1);

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

    public static float grad(int hash, final float x, final float y, final float z) {
        hash &= 15;
        final float u = hash < 8 ? x : y;
        final float v = hash < 4 ? y : hash == 12 || hash == 14 ? x :
            z;

        return ((hash & 1) == 0 ? u : -u) + ((hash & 2) == 0 ? v : -v);
    }

    public abstract float getNoise2D(float x, float z);

    public abstract float getNoise3D(float x, float y, float z);

    public float noise2D(final float x, final float z) {
        return this.noise2D(x, z, false);
    }

    public float noise2D(float x, float z, final boolean normalized) {
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

    public float noise3D(final float x, final float y, final float z) {
        return this.noise3D(x, y, z, false);
    }

    public float noise3D(float x, float y, float z, final boolean normalized) {
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

    public void setOffset(final float x, final float y, final float z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }

}
