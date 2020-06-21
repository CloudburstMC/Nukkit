package cn.nukkit.level.generator.noise.nukkit.d;

import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SimplexD extends PerlinD {

    public static final int[][] grad3 = {
        {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
        {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
        {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };

    protected static double SQRT_3;

    protected static double SQRT_5;

    protected static double F2;

    protected static double G2;

    protected static double G22;

    protected static double F3;

    protected static double G3;

    protected static double F4;

    protected static double G4;

    protected static double G42;

    protected static double G43;

    protected static double G44;

    protected final double offsetW;

    public SimplexD(final NukkitRandom random, final double octaves, final double persistence) {
        super(random, octaves, persistence);
        this.offsetW = random.nextDouble() * 256;
        SimplexD.SQRT_3 = Math.sqrt(3);
        SimplexD.SQRT_5 = Math.sqrt(5);
        SimplexD.F2 = 0.5 * (SimplexD.SQRT_3 - 1);
        SimplexD.G2 = (3 - SimplexD.SQRT_3) / 6;
        SimplexD.G22 = SimplexD.G2 * 2.0 - 1;
        SimplexD.F3 = 1.0 / 3.0;
        SimplexD.G3 = 1.0 / 6.0;
        SimplexD.F4 = (SimplexD.SQRT_5 - 1.0) / 4.0;
        SimplexD.G4 = (5.0 - SimplexD.SQRT_5) / 20.0;
        SimplexD.G42 = SimplexD.G4 * 2.0;
        SimplexD.G43 = SimplexD.G4 * 3.0;
        SimplexD.G44 = SimplexD.G4 * 4.0 - 1.0;
    }

    public SimplexD(final NukkitRandom random, final double octaves, final double persistence, final double expansion) {
        super(random, octaves, persistence, expansion);
        this.offsetW = random.nextDouble() * 256;
        SimplexD.SQRT_3 = Math.sqrt(3);
        SimplexD.SQRT_5 = Math.sqrt(5);
        SimplexD.F2 = 0.5 * (SimplexD.SQRT_3 - 1);
        SimplexD.G2 = (3 - SimplexD.SQRT_3) / 6;
        SimplexD.G22 = SimplexD.G2 * 2.0 - 1;
        SimplexD.F3 = 1.0 / 3.0;
        SimplexD.G3 = 1.0 / 6.0;
        SimplexD.F4 = (SimplexD.SQRT_5 - 1.0) / 4.0;
        SimplexD.G4 = (5.0 - SimplexD.SQRT_5) / 20.0;
        SimplexD.G42 = SimplexD.G4 * 2.0;
        SimplexD.G43 = SimplexD.G4 * 3.0;
        SimplexD.G44 = SimplexD.G4 * 4.0 - 1.0;
    }

    protected static double dot2D(final int[] g, final double x, final double y) {
        return g[0] * x + g[1] * y;
    }

    protected static double dot3D(final int[] g, final double x, final double y, final double z) {
        return g[0] * x + g[1] * y + g[2] * z;
    }

    protected static double dot4D(final int[] g, final double x, final double y, final double z, final double w) {
        return g[0] * x + g[1] * y + g[2] * z + g[3] * w;
    }

    @Override
    public double getNoise2D(double x, double y) {
        x += this.offsetX;
        y += this.offsetY;

        // Skew the input space to determine which simplex cell we're in
        final double s = (x + y) * SimplexD.F2; // Hairy factor for 2D
        final int i = (int) (x + s);
        final int j = (int) (y + s);
        final double t = (i + j) * SimplexD.G2;
        // Unskew the cell origin back to (x,y) space
        final double x0 = x - (i - t); // The x,y distances from the cell origin
        final double y0 = y - (j - t);

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

        final double x1 = x0 - i1 + SimplexD.G2; // Offsets for middle corner in (x,y) unskewed coords
        final double y1 = y0 - j1 + SimplexD.G2;
        final double x2 = x0 + SimplexD.G22; // Offsets for last corner in (x,y) unskewed coords
        final double y2 = y0 + SimplexD.G22;

        // Work out the hashed gradient indices of the three simplex corners
        final int ii = i & 255;
        final int jj = j & 255;

        double n = 0;

        // Calculate the contribution from the three corners
        final double t0 = 0.5 - x0 * x0 - y0 * y0;
        if (t0 > 0) {
            final int[] gi0 = SimplexD.grad3[this.perm[ii + this.perm[jj]] % 12];
            n += t0 * t0 * t0 * t0 * (gi0[0] * x0 + gi0[1] * y0); // (x,y) of grad3 used for 2D gradient
        }

        final double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 > 0) {
            final int[] gi1 = SimplexD.grad3[this.perm[ii + i1 + this.perm[jj + j1]] % 12];
            n += t1 * t1 * t1 * t1 * (gi1[0] * x1 + gi1[1] * y1);
        }

        final double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 > 0) {
            final int[] gi2 = SimplexD.grad3[this.perm[ii + 1 + this.perm[jj + 1]] % 12];
            n += t2 * t2 * t2 * t2 * (gi2[0] * x2 + gi2[1] * y2);
        }

        // Add contributions from each corner to get the noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0 * n;
    }

    @Override
    public double getNoise3D(double x, double y, double z) {
        x += this.offsetX;
        y += this.offsetY;
        z += this.offsetZ;

        // Skew the input space to determine which simplex cell we're in
        final double s = (x + y + z) * SimplexD.F3; // Very nice and simple skew factor for 3D
        final int i = (int) (x + s);
        final int j = (int) (y + s);
        final int k = (int) (z + s);
        final double t = (i + j + k) * SimplexD.G3;
        // Unskew the cell origin back to (x,y,z) space
        final double x0 = x - (i - t); // The x,y,z distances from the cell origin
        final double y0 = y - (j - t);
        final double z0 = z - (k - t);

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
        final double x1 = x0 - i1 + SimplexD.G3; // Offsets for second corner in (x,y,z) coords
        final double y1 = y0 - j1 + SimplexD.G3;
        final double z1 = z0 - k1 + SimplexD.G3;
        final double x2 = x0 - i2 + 2.0 * SimplexD.G3; // Offsets for third corner in (x,y,z) coords
        final double y2 = y0 - j2 + 2.0 * SimplexD.G3;
        final double z2 = z0 - k2 + 2.0 * SimplexD.G3;
        final double x3 = x0 - 1.0 + 3.0 * SimplexD.G3; // Offsets for last corner in (x,y,z) coords
        final double y3 = y0 - 1.0 + 3.0 * SimplexD.G3;
        final double z3 = z0 - 1.0 + 3.0 * SimplexD.G3;

        // Work out the hashed gradient indices of the four simplex corners
        final int ii = i & 255;
        final int jj = j & 255;
        final int kk = k & 255;

        double n = 0;

        // Calculate the contribution from the four corners
        final double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 > 0) {
            final int[] gi0 = SimplexD.grad3[this.perm[ii + this.perm[jj + this.perm[kk]]] % 12];
            n += t0 * t0 * t0 * t0 * (gi0[0] * x0 + gi0[1] * y0 + gi0[2] * z0);
        }

        final double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 > 0) {
            final int[] gi1 = SimplexD.grad3[this.perm[ii + i1 + this.perm[jj + j1 + this.perm[kk + k1]]] % 12];
            n += t1 * t1 * t1 * t1 * (gi1[0] * x1 + gi1[1] * y1 + gi1[2] * z1);
        }

        final double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 > 0) {
            final int[] gi2 = SimplexD.grad3[this.perm[ii + i2 + this.perm[jj + j2 + this.perm[kk + k2]]] % 12];
            n += t2 * t2 * t2 * t2 * (gi2[0] * x2 + gi2[1] * y2 + gi2[2] * z2);
        }

        final double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 > 0) {
            final int[] gi3 = SimplexD.grad3[this.perm[ii + 1 + this.perm[jj + 1 + this.perm[kk + 1]]] % 12];
            n += t3 * t3 * t3 * t3 * (gi3[0] * x3 + gi3[1] * y3 + gi3[2] * z3);
        }

        // Add contributions from each corner to get the noise value.
        // The result is scaled to stay just inside [-1,1]
        return 32.0 * n;
    }

}
