package cn.nukkit.level.generator.noise.vanilla.f;

import cn.nukkit.math.NukkitRandom;

public class NoiseGeneratorPerlinF {
    private final NoiseGeneratorSimplexF[] noiseLevels;
    private final int levels;

    public NoiseGeneratorPerlinF(NukkitRandom p_i45470_1_, int p_i45470_2_) {
        this.levels = p_i45470_2_;
        this.noiseLevels = new NoiseGeneratorSimplexF[p_i45470_2_];

        for (int i = 0; i < p_i45470_2_; ++i) {
            this.noiseLevels[i] = new NoiseGeneratorSimplexF(p_i45470_1_);
        }
    }

    public float getValue(float p_151601_1_, float p_151601_3_) {
        float d0 = 0.0f;
        float d1 = 1.0f;

        for (int i = 0; i < this.levels; ++i) {
            d0 += this.noiseLevels[i].getValue(p_151601_1_ * d1, p_151601_3_ * d1) / d1;
            d1 /= 2.0f;
        }

        return d0;
    }

    public float[] getRegion(float[] p_151599_1_, float p_151599_2_, float p_151599_4_, int p_151599_6_, int p_151599_7_, float p_151599_8_, float p_151599_10_, float p_151599_12_) {
        return this.getRegion(p_151599_1_, p_151599_2_, p_151599_4_, p_151599_6_, p_151599_7_, p_151599_8_, p_151599_10_, p_151599_12_, 0.5f);
    }

    public float[] getRegion(float[] p_151600_1_, float p_151600_2_, float p_151600_4_, int p_151600_6_, int p_151600_7_, float p_151600_8_, float p_151600_10_, float p_151600_12_, float p_151600_14_) {
        if (p_151600_1_ != null && p_151600_1_.length >= p_151600_6_ * p_151600_7_) {
            for (int i = 0; i < p_151600_1_.length; ++i) {
                p_151600_1_[i] = 0.0f;
            }
        } else {
            p_151600_1_ = new float[p_151600_6_ * p_151600_7_];
        }

        float d1 = 1.0f;
        float d0 = 1.0f;

        for (int j = 0; j < this.levels; ++j) {
            this.noiseLevels[j].add(p_151600_1_, p_151600_2_, p_151600_4_, p_151600_6_, p_151600_7_, p_151600_8_ * d0 * d1, p_151600_10_ * d0 * d1, 0.55f / d1);
            d0 *= p_151600_12_;
            d1 *= p_151600_14_;
        }

        return p_151600_1_;
    }
}
