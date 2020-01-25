package cn.nukkit.math;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An implementation of {@link Random} that is not thread-safe.
 * <p>
 * Primarily based on {@link java.util.SplittableRandom}.
 * <p>
 * Retains some methods for backwards-compatibility with the original implementation of this class (by Angelic47).
 *
 * @see java.util.SplittableRandom
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class NukkitRandom extends Random {
    private static final long   GAMMA       = 0x9e3779b97f4a7c15L;
    private static final double DOUBLE_UNIT = 0x1.0p-53;

    private static final AtomicLong BASE_SEED = new AtomicLong(mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime()));

    private static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }

    private static int mix32(long z) {
        z = (z ^ (z >>> 33)) * 0x62a9d9ed799705f5L;
        return (int) (((z ^ (z >>> 28)) * 0xcb24d0a5c88c35b3L) >>> 32);
    }

    private static long mixGamma(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        z = (z ^ (z >>> 33)) | 1L;
        int n = Long.bitCount(z ^ (z >>> 1));
        return (n < 24) ? z ^ 0xaaaaaaaaaaaaaaaaL : z;
    }

    private       long seed;
    private final long gamma;

    public NukkitRandom() {
        long s = BASE_SEED.getAndAdd(GAMMA * 2);
        this.seed = mix64(s);
        this.gamma = mixGamma(s + GAMMA);
    }

    public NukkitRandom(long seed) {
        this(seed, GAMMA);
    }

    private long nextSeed() {
        return this.seed += this.gamma;
    }

    /**
     * @see SplittableRandom#split()
     */
    public NukkitRandom split() {
        return new NukkitRandom(this.nextLong(), mixGamma(this.nextSeed()));
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void nextBytes(byte[] bytes) {
        super.nextBytes(bytes);
    }

    @Override
    protected int next(int bits) {
        return this.nextInt(1 << bits);
    }

    @Override
    public int nextInt() {
        return mix32(this.nextSeed());
    }

    @Override
    public int nextInt(int bound) {
        Preconditions.checkArgument(bound > 0, "bound must be positive");

        int r = this.nextInt();
        int m = bound - 1;
        if ((bound & m) == 0) {
            r &= m;
        } else {
            int u = r >>> 1;
            while (u + m - (r = u % bound) < 0) {
                u = this.nextInt() >>> 1;
            }
        }
        return r;
    }

    public int nextInt(int origin, int bound) {
        Preconditions.checkArgument(bound > origin, "bound must be greater than origin");

        return this.nextInt(bound - origin) + origin;
    }

    @Override
    public long nextLong() {
        return mix64(this.nextSeed());
    }

    public long nextLong(long bound) {
        Preconditions.checkArgument(bound > 0L, "bound must be positive");

        long r = mix64(this.nextSeed());
        long m = bound - 1L;
        if ((bound & m) == 0L) {
            r &= m;
        } else {
            long u = r >>> 1;
            while (u + m - (r = u % bound) < 0L) {
                u = this.nextLong() >>> 1L;
            }
        }
        return r;
    }

    public long nextLong(long origin, long bound) {
        Preconditions.checkArgument(bound > origin, "bound must be greater than origin");

        return this.nextLong(bound - origin) + origin;
    }

    @Override
    public boolean nextBoolean() {
        return this.nextInt() < 0;
    }

    @Override
    public double nextDouble() {
        return (this.nextLong() >>> 11L) * DOUBLE_UNIT;
    }

    public double nextDouble(double bound) {
        Preconditions.checkArgument(bound > 0.0d, "bound must be positive");

        double result = (this.nextLong() >>> 11L) * DOUBLE_UNIT * bound;
        return (result < bound) ? result : Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1L);
    }

    public double nextDouble(double origin, double bound) {
        Preconditions.checkArgument(bound > origin, "bound must be positive");

        return this.nextDouble(bound - origin) + origin;
    }
}
