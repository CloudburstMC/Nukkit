package cn.nukkit.level.generator.noise.nukkit.f;

import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public class PerlinF extends NoiseF {
    public PerlinF(NukkitRandom random, float octaves, float persistence) {
        this(random, octaves, persistence, 1);
    }

    public PerlinF(NukkitRandom random, float octaves, float persistence, float expansion) {
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
            int pos = random.nextBoundedInt(256 - i) + i;
            int old = this.perm[i];
            this.perm[i] = this.perm[pos];
            this.perm[pos] = old;
            this.perm[i + 256] = this.perm[i];
        }
    }

    @Override
    public float getNoise2D(float x, float y) {
        return this.getNoise3D(x, y, 0);
    }

    @Override
    public float getNoise3D(float x, float y, float z) {
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
        float fX = x * x * x * (x * (x * 6 - 15) + 10);
        float fY = y * y * y * (y * (y * 6 - 15) + 10);
        float fZ = z * z * z * (z * (z * 6 - 15) + 10);

        //Cube corners
        int A = this.perm[X] + Y;
        int B = this.perm[X + 1] + Y;

        int AA = this.perm[A] + Z;
        int AB = this.perm[A + 1] + Z;
        int BA = this.perm[B] + Z;
        int BB = this.perm[B + 1] + Z;

        float AA1 = grad(this.perm[AA], x, y, z);
        float BA1 = grad(this.perm[BA], x - 1, y, z);
        float AB1 = grad(this.perm[AB], x, y - 1, z);
        float BB1 = grad(this.perm[BB], x - 1, y - 1, z);
        float AA2 = grad(this.perm[AA + 1], x, y, z - 1);
        float BA2 = grad(this.perm[BA + 1], x - 1, y, z - 1);
        float AB2 = grad(this.perm[AB + 1], x, y - 1, z - 1);
        float BB2 = grad(this.perm[BB + 1], x - 1, y - 1, z - 1);

        float xLerp11 = AA1 + fX * (BA1 - AA1);

        float zLerp1 = xLerp11 + fY * (AB1 + fX * (BB1 - AB1) - xLerp11);

        float xLerp21 = AA2 + fX * (BA2 - AA2);

        return zLerp1 + fZ * (xLerp21 + fY * (AB2 + fX * (BB2 - AB2) - xLerp21) - zLerp1);

    }
}
