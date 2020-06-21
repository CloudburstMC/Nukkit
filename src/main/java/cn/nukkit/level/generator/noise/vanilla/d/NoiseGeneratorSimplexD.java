package cn.nukkit.level.generator.noise.vanilla.d;

import cn.nukkit.math.NukkitRandom;

public class NoiseGeneratorSimplexD {

    public static final double SQRT_3 = Math.sqrt(3.0D);

    private static final int[][] grad3 = {{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};

    private static final double F2 = 0.5D * (NoiseGeneratorSimplexD.SQRT_3 - 1.0D);

    private static final double G2 = (3.0D - NoiseGeneratorSimplexD.SQRT_3) / 6.0D;

    private final int[] p;

    public double xo;

    public double yo;

    public double zo;

    public NoiseGeneratorSimplexD() {
        this(new NukkitRandom(System.currentTimeMillis()));
    }

    public NoiseGeneratorSimplexD(final NukkitRandom p_i45471_1_) {
        this.p = new int[512];
        this.xo = p_i45471_1_.nextDouble() * 256.0D;
        this.yo = p_i45471_1_.nextDouble() * 256.0D;
        this.zo = p_i45471_1_.nextDouble() * 256.0D;

        int i = 0;
        while (i < 256) {
            this.p[i] = i++;
        }

        for (int l = 0; l < 256; ++l) {
            final int j = p_i45471_1_.nextBoundedInt(256 - l) + l;
            final int k = this.p[l];
            this.p[l] = this.p[j];
            this.p[j] = k;
            this.p[l + 256] = this.p[l];
        }
    }

    private static int fastFloor(final double value) {
        return value > 0.0D ? (int) value : (int) value - 1;
    }

    private static double dot(final int[] p_151604_0_, final double p_151604_1_, final double p_151604_3_) {
        return (double) p_151604_0_[0] * p_151604_1_ + (double) p_151604_0_[1] * p_151604_3_;
    }

    public double getValue(final double p_151605_1_, final double p_151605_3_) {
        final double d3 = 0.5D * (NoiseGeneratorSimplexD.SQRT_3 - 1.0D);
        final double d4 = (p_151605_1_ + p_151605_3_) * d3;
        final int i = NoiseGeneratorSimplexD.fastFloor(p_151605_1_ + d4);
        final int j = NoiseGeneratorSimplexD.fastFloor(p_151605_3_ + d4);
        final double d5 = (3.0D - NoiseGeneratorSimplexD.SQRT_3) / 6.0D;
        final double d6 = (double) (i + j) * d5;
        final double d7 = (double) i - d6;
        final double d8 = (double) j - d6;
        final double d9 = p_151605_1_ - d7;
        final double d10 = p_151605_3_ - d8;
        final int k;
        final int l;

        if (d9 > d10) {
            k = 1;
            l = 0;
        } else {
            k = 0;
            l = 1;
        }

        final double d11 = d9 - (double) k + d5;
        final double d12 = d10 - (double) l + d5;
        final double d13 = d9 - 1.0D + 2.0D * d5;
        final double d14 = d10 - 1.0D + 2.0D * d5;
        final int i1 = i & 255;
        final int j1 = j & 255;
        final int k1 = this.p[i1 + this.p[j1]] % 12;
        final int l1 = this.p[i1 + k + this.p[j1 + l]] % 12;
        final int i2 = this.p[i1 + 1 + this.p[j1 + 1]] % 12;
        double d15 = 0.5D - d9 * d9 - d10 * d10;
        final double d0;

        if (d15 < 0.0D) {
            d0 = 0.0D;
        } else {
            d15 = d15 * d15;
            d0 = d15 * d15 * NoiseGeneratorSimplexD.dot(NoiseGeneratorSimplexD.grad3[k1], d9, d10);
        }

        double d16 = 0.5D - d11 * d11 - d12 * d12;
        final double d1;

        if (d16 < 0.0D) {
            d1 = 0.0D;
        } else {
            d16 = d16 * d16;
            d1 = d16 * d16 * NoiseGeneratorSimplexD.dot(NoiseGeneratorSimplexD.grad3[l1], d11, d12);
        }

        double d17 = 0.5D - d13 * d13 - d14 * d14;
        final double d2;

        if (d17 < 0.0D) {
            d2 = 0.0D;
        } else {
            d17 = d17 * d17;
            d2 = d17 * d17 * NoiseGeneratorSimplexD.dot(NoiseGeneratorSimplexD.grad3[i2], d13, d14);
        }

        return 70.0D * (d0 + d1 + d2);
    }

    public void add(final double[] p_151606_1_, final double p_151606_2_, final double p_151606_4_, final int p_151606_6_, final int p_151606_7_, final double p_151606_8_, final double p_151606_10_, final double p_151606_12_) {
        int i = 0;

        for (int j = 0; j < p_151606_7_; ++j) {
            final double d0 = (p_151606_4_ + (double) j) * p_151606_10_ + this.yo;

            for (int k = 0; k < p_151606_6_; ++k) {
                final double d1 = (p_151606_2_ + (double) k) * p_151606_8_ + this.xo;
                final double d5 = (d1 + d0) * NoiseGeneratorSimplexD.F2;
                final int l = NoiseGeneratorSimplexD.fastFloor(d1 + d5);
                final int i1 = NoiseGeneratorSimplexD.fastFloor(d0 + d5);
                final double d6 = (double) (l + i1) * NoiseGeneratorSimplexD.G2;
                final double d7 = (double) l - d6;
                final double d8 = (double) i1 - d6;
                final double d9 = d1 - d7;
                final double d10 = d0 - d8;
                final int j1;
                final int k1;

                if (d9 > d10) {
                    j1 = 1;
                    k1 = 0;
                } else {
                    j1 = 0;
                    k1 = 1;
                }

                final double d11 = d9 - (double) j1 + NoiseGeneratorSimplexD.G2;
                final double d12 = d10 - (double) k1 + NoiseGeneratorSimplexD.G2;
                final double d13 = d9 - 1.0D + 2.0D * NoiseGeneratorSimplexD.G2;
                final double d14 = d10 - 1.0D + 2.0D * NoiseGeneratorSimplexD.G2;
                final int l1 = l & 255;
                final int i2 = i1 & 255;
                final int j2 = this.p[l1 + this.p[i2]] % 12;
                final int k2 = this.p[l1 + j1 + this.p[i2 + k1]] % 12;
                final int l2 = this.p[l1 + 1 + this.p[i2 + 1]] % 12;
                double d15 = 0.5D - d9 * d9 - d10 * d10;
                final double d2;

                if (d15 < 0.0D) {
                    d2 = 0.0D;
                } else {
                    d15 = d15 * d15;
                    d2 = d15 * d15 * NoiseGeneratorSimplexD.dot(NoiseGeneratorSimplexD.grad3[j2], d9, d10);
                }

                double d16 = 0.5D - d11 * d11 - d12 * d12;
                final double d3;

                if (d16 < 0.0D) {
                    d3 = 0.0D;
                } else {
                    d16 = d16 * d16;
                    d3 = d16 * d16 * NoiseGeneratorSimplexD.dot(NoiseGeneratorSimplexD.grad3[k2], d11, d12);
                }

                double d17 = 0.5D - d13 * d13 - d14 * d14;
                final double d4;

                if (d17 < 0.0D) {
                    d4 = 0.0D;
                } else {
                    d17 = d17 * d17;
                    d4 = d17 * d17 * NoiseGeneratorSimplexD.dot(NoiseGeneratorSimplexD.grad3[l2], d13, d14);
                }

                final int i3 = i++;
                p_151606_1_[i3] += 70.0D * (d2 + d3 + d4) * p_151606_12_;
            }
        }
    }

}
