package cn.nukkit.level.generator.noise.nukkit.f;

import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class SimplexF extends PerlinF {

    public static final int[][] grad3 = {
        {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
        {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
        {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };

    protected static float SQRT_3;

    protected static float SQRT_5;

    protected static float F2;

    protected static float G2;

    protected static float G22;

    protected static float F3;

    protected static float G3;

    protected static float F4;

    protected static float G4;

    protected static float G42;

    protected static float G43;

    protected static float G44;

    protected final float offsetW;

    public SimplexF(final NukkitRandom random, final float octaves, final float persistence) {
        super(random, octaves, persistence);
        this.offsetW = random.nextFloat() * 256;
        SimplexF.SQRT_3 = (float) Math.sqrt(3);
        SimplexF.SQRT_5 = (float) Math.sqrt(5);
        SimplexF.F2 = 0.5f * (SimplexF.SQRT_3 - 1f);
        SimplexF.G2 = (3f - SimplexF.SQRT_3) / 6f;
        SimplexF.G22 = SimplexF.G2 * 2.0f - 1f;
        SimplexF.F3 = 1.0f / 3.0f;
        SimplexF.G3 = 1.0f / 6.0f;
        SimplexF.F4 = (SimplexF.SQRT_5 - 1.0f) / 4.0f;
        SimplexF.G4 = (5.0f - SimplexF.SQRT_5) / 20.0f;
        SimplexF.G42 = SimplexF.G4 * 2.0f;
        SimplexF.G43 = SimplexF.G4 * 3.0f;
        SimplexF.G44 = SimplexF.G4 * 4.0f - 1.0f;
    }

    public SimplexF(final NukkitRandom random, final float octaves, final float persistence, final float expansion) {
        super(random, octaves, persistence, expansion);
        this.offsetW = random.nextFloat() * 256;
        SimplexF.SQRT_3 = (float) Math.sqrt(3);
        SimplexF.SQRT_5 = (float) Math.sqrt(5);
        SimplexF.F2 = 0.5f * (SimplexF.SQRT_3 - 1f);
        SimplexF.G2 = (3f - SimplexF.SQRT_3) / 6f;
        SimplexF.G22 = SimplexF.G2 * 2.0f - 1f;
        SimplexF.F3 = 1.0f / 3.0f;
        SimplexF.G3 = 1.0f / 6.0f;
        SimplexF.F4 = (SimplexF.SQRT_5 - 1.0f) / 4.0f;
        SimplexF.G4 = (5.0f - SimplexF.SQRT_5) / 20.0f;
        SimplexF.G42 = SimplexF.G4 * 2.0f;
        SimplexF.G43 = SimplexF.G4 * 3.0f;
        SimplexF.G44 = SimplexF.G4 * 4.0f - 1.0f;
    }

    protected static float dot2D(final int[] g, final float x, final float y) {
        return g[0] * x + g[1] * y;
    }

    protected static float dot3D(final int[] g, final float x, final float y, final float z) {
        return g[0] * x + g[1] * y + g[2] * z;
    }

    protected static float dot4D(final int[] g, final float x, final float y, final float z, final float w) {
        return g[0] * x + g[1] * y + g[2] * z + g[3] * w;
    }

    @Override
    public float getNoise2D(float x, float y) {
        x += this.offsetX;
        y += this.offsetY;

        // Skew the input space to determine which simplex cell we're in
        final float s = (x + y) * SimplexF.F2; // Hairy factor for 2D
        final int i = (int) (x + s);
        final int j = (int) (y + s);
        final float t = (i + j) * SimplexF.G2;
        // Unskew the cell origin back to (x,y) space
        final float x0 = x - (i - t); // The x,y distances from the cell origin
        final float y0 = y - (j - t);

        // For the 2D case, the simplex shape is an equilateral triangle.
        int i1 = 0;
        int j1 = 0;
        // Determine which simplex we are in.
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } // lower triangle, XY order: (0,0).(1,0).(1,1)
        else {
            i1 = 0;
            j1 = 1;
        }
        // upper triangle, YX order: (0,0).(0,1).(1,1)

        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6

        final float x1 = x0 - i1 + SimplexF.G2; // Offsets for middle corner in (x,y) unskewed coords
        final float y1 = y0 - j1 + SimplexF.G2;
        final float x2 = x0 + SimplexF.G22; // Offsets for last corner in (x,y) unskewed coords
        final float y2 = y0 + SimplexF.G22;

        // Work out the hashed gradient indices of the three simplex corners
        final int ii = i & 255;
        final int jj = j & 255;

        float n = 0;

        // Calculate the contribution from the three corners
        final float t0 = 0.5f - x0 * x0 - y0 * y0;
        if (t0 > 0) {
            final int[] gi0 = SimplexF.grad3[this.perm[ii + this.perm[jj]] % 12];
            n += t0 * t0 * t0 * t0 * (gi0[0] * x0 + gi0[1] * y0); // (x,y) of grad3 used for 2D gradient
        }

        final float t1 = 0.5f - x1 * x1 - y1 * y1;
        if (t1 > 0) {
            final int[] gi1 = SimplexF.grad3[this.perm[ii + i1 + this.perm[jj + j1]] % 12];
            n += t1 * t1 * t1 * t1 * (gi1[0] * x1 + gi1[1] * y1);
        }

        final float t2 = 0.5f - x2 * x2 - y2 * y2;
        if (t2 > 0) {
            final int[] gi2 = SimplexF.grad3[this.perm[ii + 1 + this.perm[jj + 1]] % 12];
            n += t2 * t2 * t2 * t2 * (gi2[0] * x2 + gi2[1] * y2);
        }

        // Add contributions from each corner to get the noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0f * n;
    }

    @Override
    public float getNoise3D(float x, float y, float z) {
        x += this.offsetX;
        y += this.offsetY;
        z += this.offsetZ;

        // Skew the input space to determine which simplex cell we're in
        final float s = (x + y + z) * SimplexF.F3; // Very nice and simple skew factor for 3D
        final int i = (int) (x + s);
        final int j = (int) (y + s);
        final int k = (int) (z + s);
        final float t = (i + j + k) * SimplexF.G3;
        // Unskew the cell origin back to (x,y,z) space
        final float x0 = x - (i - t); // The x,y,z distances from the cell origin
        final float y0 = y - (j - t);
        final float z0 = z - (k - t);

        // For the 3D case, the simplex shape is a slightly irregular tetrahedron.
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;

        // Determine which simplex we are in.
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } // X Y Z order
            else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } // X Z Y order
            else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
            // Z X Y order
        } else { // x0<y0
            if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } // Z Y X order
            else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } // Y Z X order
            else {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
            // Y X Z order
        }

        // A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
        // a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
        // a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
        // c = 1/6.
        final float x1 = x0 - i1 + SimplexF.G3; // Offsets for second corner in (x,y,z) coords
        final float y1 = y0 - j1 + SimplexF.G3;
        final float z1 = z0 - k1 + SimplexF.G3;
        final float x2 = x0 - i2 + 2.0f * SimplexF.G3; // Offsets for third corner in (x,y,z) coords
        final float y2 = y0 - j2 + 2.0f * SimplexF.G3;
        final float z2 = z0 - k2 + 2.0f * SimplexF.G3;
        final float x3 = x0 - 1.0f + 3.0f * SimplexF.G3; // Offsets for last corner in (x,y,z) coords
        final float y3 = y0 - 1.0f + 3.0f * SimplexF.G3;
        final float z3 = z0 - 1.0f + 3.0f * SimplexF.G3;

        // Work out the hashed gradient indices of the four simplex corners
        final int ii = i & 255;
        final int jj = j & 255;
        final int kk = k & 255;

        float n = 0;

        // Calculate the contribution from the four corners
        final float t0 = 0.6f - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 > 0) {
            final int[] gi0 = SimplexF.grad3[this.perm[ii + this.perm[jj + this.perm[kk]]] % 12];
            n += t0 * t0 * t0 * t0 * (gi0[0] * x0 + gi0[1] * y0 + gi0[2] * z0);
        }

        final float t1 = 0.6f - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 > 0) {
            final int[] gi1 = SimplexF.grad3[this.perm[ii + i1 + this.perm[jj + j1 + this.perm[kk + k1]]] % 12];
            n += t1 * t1 * t1 * t1 * (gi1[0] * x1 + gi1[1] * y1 + gi1[2] * z1);
        }

        final float t2 = 0.6f - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 > 0) {
            final int[] gi2 = SimplexF.grad3[this.perm[ii + i2 + this.perm[jj + j2 + this.perm[kk + k2]]] % 12];
            n += t2 * t2 * t2 * t2 * (gi2[0] * x2 + gi2[1] * y2 + gi2[2] * z2);
        }

        final float t3 = 0.6f - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 > 0) {
            final int[] gi3 = SimplexF.grad3[this.perm[ii + 1 + this.perm[jj + 1 + this.perm[kk + 1]]] % 12];
            n += t3 * t3 * t3 * t3 * (gi3[0] * x3 + gi3[1] * y3 + gi3[2] * z3);
        }

        // Add contributions from each corner to get the noise value.
        // The result is scaled to stay just inside [-1,1]
        return 32.0f * n;
    }

}
