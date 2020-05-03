package cn.nukkit.math;

import net.daporkchop.lib.random.impl.AbstractFastPRandom;

import com.nukkitx.math.vector.Vector3f;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Bedrock-style PRNG.
 * <p>
 * Warning: very slow!
 */
public class BedrockRandom extends AbstractFastPRandom {
    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
    private static final ThreadLocal<BedrockRandom> THREAD_LOCAL = ThreadLocal.withInitial(BedrockRandom::new);

    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;
    private static final int N = 624;
    private static final int M = 397;
    private static final int[] MAGIC = {0, 0x9908b0df};
    private static final int MAGIC_FACTOR_1 = 1812433253;
    private static final int MAGIC_MASK1 = 0x9d2c5680;
    private static final int MAGIC_MASK2 = 0xefc60000;
    private static final int DEFAULT_SEED = 5489;

    private final int[] mt = new int[N];
    private int seed;
    private int mtiFast;
    private int mti;
    private boolean hasFutureGaussian;
    private float futureGaussian;

    public BedrockRandom() {
        this((int) (seedUniquifier() ^ System.nanoTime()));
    }

    public BedrockRandom(int seed) {
        this.setSeed(seed);
    }

    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for (; ; ) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }

    public static BedrockRandom getThreadLocal() {
        return THREAD_LOCAL.get();
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
        this.mtiFast = N + 1;
        this.hasFutureGaussian = false;
        this.futureGaussian = 0;
        this.initGenRandFast(seed);
    }

    @Override
    public void setSeed(long seed) {
        this.setSeed((int) seed);
    }

    @Override
    public int nextInt() {
        return this.genRandInt32();
    }

    @Override
    public long nextLong() {
        return (((long) this.genRandInt32()) << 32L) | (long) this.genRandInt32();
    }

    public Vector3f nextVector3() {
        float x = this.nextFloat();
        float y = this.nextFloat();
        float z = this.nextFloat();
        return Vector3f.from(x, y, z);
    }

    public Vector3f nextGaussianVector3() {
        float x = this.nextGaussianFloat();
        float y = this.nextGaussianFloat();
        float z = this.nextGaussianFloat();
        return Vector3f.from(x, y, z);
    }

    private void initGenRand(int initialValue) {
        int lastValue = this.mt[0] = initialValue;
        for (this.mtiFast = 1; this.mtiFast < N; ++this.mtiFast) {
            this.mt[this.mtiFast] = lastValue = this.mtiFast + MAGIC_FACTOR_1 * ((lastValue >>> 30) ^ lastValue);
        }
        this.mtiFast = N;
    }

    private void initGenRandFast(int initialValue) {
        int lastValue = this.mt[0] = initialValue;
        for (int i = 1; i <= M; i++) {
            this.mt[i] = lastValue = i + MAGIC_FACTOR_1 * ((lastValue >>> 30) ^ lastValue);
        }
        this.mtiFast = N;
        this.mti = M + 1;
    }

    private int genRandInt32() {
        if (this.mti == N) {
            this.mti = 0;
        } else if (this.mti > N) {
            this.initGenRand(DEFAULT_SEED);
        }

        if (this.mti >= N - M) {
            if (this.mti >= N - 1) {
                this.mt[N - 1] = MAGIC[this.mt[0] & 1] ^
                        ((this.mt[0] & LOWER_MASK | this.mt[N - 1] & UPPER_MASK) >>> 1) ^
                        this.mt[M - 1];
            } else {
                this.mt[this.mti] = MAGIC[this.mt[this.mti + 1] & 1] ^
                        ((this.mt[this.mti + 1] & LOWER_MASK | this.mt[this.mti] & UPPER_MASK) >>> 1) ^
                        this.mt[this.mti - (N - M)];
            }
        } else {
            this.mt[this.mti] = MAGIC[this.mt[this.mti + 1] & 1] ^ ((this.mt[this.mti + 1] & LOWER_MASK | this.mt[this.mti] & UPPER_MASK) >> 1) ^ this.mt[this.mti + M];

            if (this.mtiFast < N) {
                this.mt[this.mtiFast] = this.mtiFast + MAGIC_FACTOR_1 *
                        ((this.mt[this.mtiFast - 1] >>> 30) ^ this.mt[this.mtiFast - 1]);
            }
        }

        int value = this.mt[this.mti++];
        value = ((value ^ (value >> 11)) << 7) & MAGIC_MASK1 ^ value ^ (value >> 11);
        value = (value << 15) & MAGIC_MASK2 ^ value ^ (((value << 15) & MAGIC_MASK2 ^ value) >> 18);
        return value;
    }
}
