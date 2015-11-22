package cn.nukkit.level.generator.noise;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Perlin extends Noise {
    public static int[][] grad3 = {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };

    public Perlin(Random random, double octaves, double persistence) {
        this(random, octaves, persistence, 1);
    }

    public Perlin(Random random, double octaves, double persistence, double expansion) {
        this.octaves = octaves;
        this.persistence = persistence;
        this.expansion = expansion;
        this.offsetX = random.nextFloat() * 256;
        this.offsetY = random.nextFloat() * 256;
        this.offsetZ = random.nextFloat() * 256;
        this.perm = new int[512];
        for (int i = 0; i < 256; ++i) {
            this.perm[i] = random.nextInt(256);
        }
        for (int i = 0; i < 256; ++i) {
            int pos = random.nextInt(256 - i) + i;
            int old = this.perm[i];
            this.perm[i] = this.perm[pos];
            this.perm[pos] = old;
            this.perm[i + 256] = this.perm[i];
        }
    }

    @Override
    public double getNoise2D(double x, double y) {
        return this.getNoise3D(x, y, 0);
    }

    @Override
    public double getNoise3D(double x, double y, double z) {
        x += this.offsetX;
        y += this.offsetY;
        z += this.offsetZ;

        int floorX = (int) x;
        int floorY = (int) y;
        int floorZ = (int) z;

        int X = floorX & 0xFF;
        int Y = floorY & 0xFF;
        int Z = floorZ & 0xFF;

        x -= floorX;
        y -= floorY;
        z -= floorZ;

        //Fade curves
        //fX = fade(x);
        //fY = fade(y);
        //fZ = fade(z);
        double fX = x * x * x * (x * (x * 6 - 15) + 10);
        double fY = y * y * y * (y * (y * 6 - 15) + 10);
        double fZ = z * z * z * (z * (z * 6 - 15) + 10);

        //Cube corners
        int A = this.perm[X] + Y;
        int B = this.perm[X + 1] + Y;

        int AA = this.perm[A] + Z;
        int AB = this.perm[A + 1] + Z;
        int BA = this.perm[B] + Z;
        int BB = this.perm[B + 1] + Z;

        double AA1 = grad(this.perm[AA], x, y, z);
        double BA1 = grad(this.perm[BA], x - 1, y, z);
        double AB1 = grad(this.perm[AB], x, y - 1, z);
        double BB1 = grad(this.perm[BB], x - 1, y - 1, z);
        double AA2 = grad(this.perm[AA + 1], x, y, z - 1);
        double BA2 = grad(this.perm[BA + 1], x - 1, y, z - 1);
        double AB2 = grad(this.perm[AB + 1], x, y - 1, z - 1);
        double BB2 = grad(this.perm[BB + 1], x - 1, y - 1, z - 1);

        double xLerp11 = AA1 + fX * (BA1 - AA1);

        double zLerp1 = xLerp11 + fY * (AB1 + fX * (BB1 - AB1) - xLerp11);

        double xLerp21 = AA2 + fX * (BA2 - AA2);

        return zLerp1 + fZ * (xLerp21 + fY * (AB2 + fX * (BB2 - AB2) - xLerp21) - zLerp1);

    }
}
