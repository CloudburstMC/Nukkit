package cn.nukkit.level.generator.noise.nukkit.d;

import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PerlinD extends NoiseD {

    public PerlinD(final NukkitRandom random, final double octaves, final double persistence) {
        this(random, octaves, persistence, 1);
    }

    public PerlinD(final NukkitRandom random, final double octaves, final double persistence, final double expansion) {
        this.octaves = octaves;
        this.persistence = persistence;
        this.expansion = expansion;
        this.offsetX = random.nextFloat() * 256;
        this.offsetY = random.nextFloat() * 256;
        this.offsetZ = random.nextFloat() * 256;
        this.perm = new int[512];
        for (int i = 0; i < 256; ++i) {
            this.perm[i] = random.nextBoundedInt(256);
        }
        for (int i = 0; i < 256; ++i) {
            final int pos = random.nextBoundedInt(256 - i) + i;
            final int old = this.perm[i];
            this.perm[i] = this.perm[pos];
            this.perm[pos] = old;
            this.perm[i + 256] = this.perm[i];
        }
    }

    @Override
    public double getNoise2D(final double x, final double y) {
        return this.getNoise3D(x, y, 0);
    }

    @Override
    public double getNoise3D(double x, double y, double z) {
        x += this.offsetX;
        y += this.offsetY;
        z += this.offsetZ;

        final int floorX = (int) x;
        final int floorY = (int) y;
        final int floorZ = (int) z;

        final int X = floorX & 0xFF;
        final int Y = floorY & 0xFF;
        final int Z = floorZ & 0xFF;

        x -= floorX;
        y -= floorY;
        z -= floorZ;

        //Fade curves
        //fX = fade(x);
        //fY = fade(y);
        //fZ = fade(z);
        final double fX = x * x * x * (x * (x * 6 - 15) + 10);
        final double fY = y * y * y * (y * (y * 6 - 15) + 10);
        final double fZ = z * z * z * (z * (z * 6 - 15) + 10);

        //Cube corners
        final int A = this.perm[X] + Y;
        final int B = this.perm[X + 1] + Y;

        final int AA = this.perm[A] + Z;
        final int AB = this.perm[A + 1] + Z;
        final int BA = this.perm[B] + Z;
        final int BB = this.perm[B + 1] + Z;

        final double AA1 = NoiseD.grad(this.perm[AA], x, y, z);
        final double BA1 = NoiseD.grad(this.perm[BA], x - 1, y, z);
        final double AB1 = NoiseD.grad(this.perm[AB], x, y - 1, z);
        final double BB1 = NoiseD.grad(this.perm[BB], x - 1, y - 1, z);
        final double AA2 = NoiseD.grad(this.perm[AA + 1], x, y, z - 1);
        final double BA2 = NoiseD.grad(this.perm[BA + 1], x - 1, y, z - 1);
        final double AB2 = NoiseD.grad(this.perm[AB + 1], x, y - 1, z - 1);
        final double BB2 = NoiseD.grad(this.perm[BB + 1], x - 1, y - 1, z - 1);

        final double xLerp11 = AA1 + fX * (BA1 - AA1);

        final double zLerp1 = xLerp11 + fY * (AB1 + fX * (BB1 - AB1) - xLerp11);

        final double xLerp21 = AA2 + fX * (BA2 - AA2);

        return zLerp1 + fZ * (xLerp21 + fY * (AB2 + fX * (BB2 - AB2) - xLerp21) - zLerp1);

    }

}
