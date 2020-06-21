package cn.nukkit.level.generator.noise.nukkit.f;

import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PerlinF extends NoiseF {

    public PerlinF(final NukkitRandom random, final float octaves, final float persistence) {
        this(random, octaves, persistence, 1);
    }

    public PerlinF(final NukkitRandom random, final float octaves, final float persistence, final float expansion) {
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
    public float getNoise2D(final float x, final float y) {
        return this.getNoise3D(x, y, 0);
    }

    @Override
    public float getNoise3D(float x, float y, float z) {
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
        final float fX = x * x * x * (x * (x * 6 - 15) + 10);
        final float fY = y * y * y * (y * (y * 6 - 15) + 10);
        final float fZ = z * z * z * (z * (z * 6 - 15) + 10);

        //Cube corners
        final int A = this.perm[X] + Y;
        final int B = this.perm[X + 1] + Y;

        final int AA = this.perm[A] + Z;
        final int AB = this.perm[A + 1] + Z;
        final int BA = this.perm[B] + Z;
        final int BB = this.perm[B + 1] + Z;

        final float AA1 = NoiseF.grad(this.perm[AA], x, y, z);
        final float BA1 = NoiseF.grad(this.perm[BA], x - 1, y, z);
        final float AB1 = NoiseF.grad(this.perm[AB], x, y - 1, z);
        final float BB1 = NoiseF.grad(this.perm[BB], x - 1, y - 1, z);
        final float AA2 = NoiseF.grad(this.perm[AA + 1], x, y, z - 1);
        final float BA2 = NoiseF.grad(this.perm[BA + 1], x - 1, y, z - 1);
        final float AB2 = NoiseF.grad(this.perm[AB + 1], x, y - 1, z - 1);
        final float BB2 = NoiseF.grad(this.perm[BB + 1], x - 1, y - 1, z - 1);

        final float xLerp11 = AA1 + fX * (BA1 - AA1);

        final float zLerp1 = xLerp11 + fY * (AB1 + fX * (BB1 - AB1) - xLerp11);

        final float xLerp21 = AA2 + fX * (BA2 - AA2);

        return zLerp1 + fZ * (xLerp21 + fY * (AB2 + fX * (BB2 - AB2) - xLerp21) - zLerp1);

    }

}
