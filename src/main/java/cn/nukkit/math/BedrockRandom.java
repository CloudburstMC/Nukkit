package cn.nukkit.math;

import java.util.concurrent.ThreadLocalRandom;

public class BedrockRandom {

    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;
    private static final int N = 624;
    private static final ThreadLocal<BedrockRandom> THREAD_LOCAL = ThreadLocal.withInitial(BedrockRandom::new);
    private static final int M = 397;
    private static final int[] MAGIC = {0, 0x9908b0df};
    private static final int MAGIC_FACTOR_1 = 1812433253;
    private static final int MAGIC_MASK1 = 0x9d2c5680;
    private static final int MAGIC_MASK2 = 0xefc60000;
    private static final int DEFAULT_SEED = 5489;
    private static final double TWO_POW_M32 = 1.0 / (1L << 32);
    private static final int BOOLEAN_MASK = ~0b1;
    private final int[] mt = new int[N];
    private int seed;
    private int mtiFast;
    private int mti;
    private boolean hasFutureGaussian;
    private float futureGaussian;

    public BedrockRandom() {
        this(ThreadLocalRandom.current().nextInt());
    }

    public BedrockRandom(int seed) {
        this.setSeed(seed);
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

    public int nextInt() {
        return this.genRandInt32() >>> 1;
    }

    public int nextInt(int bound) {
        return bound > 0 ? (int) (Integer.toUnsignedLong(this.genRandInt32()) % bound) : 0;
    }

    public int nextInt(int origin, int bound) {
        return origin < bound ? origin + this.nextInt(bound - origin) : origin;
    }

    public int nextIntInclusive(int origin, int bound) {
        return this.nextInt(origin, bound + 1);
    }

    public long nextUnsignedInt() {
        return Integer.toUnsignedLong(this.genRandInt32());
    }

    public short nextUnsignedByte() {
        return (short) (this.genRandInt32() & 0xff);
    }

    public boolean nextBoolean() {
        return (this.genRandInt32() & BOOLEAN_MASK) != 0;
    }

    public float nextFloat() {
        return (float) this.genRandReal2();
    }

    public float nextFloat(float bound) {
        return this.nextFloat() * bound;
    }

    public float nextFloat(float origin, float bound) {
        return origin + this.nextFloat(origin - bound);
    }

    public double nextDouble() {
        return this.genRandReal2();
    }

    public double nextGaussian() {
        if (this.hasFutureGaussian) {
            this.hasFutureGaussian = false;
            return futureGaussian;
        }

        float v1, v2, s;
        do {
            v1 = this.nextFloat() * 2 - 1;
            v2 = this.nextFloat() * 2 - 1;
            s = v1 * v1 + v2 * v2;
        } while (s == 0 || s > 1);

        float multiplier = (float) Math.sqrt(-2 * (float) Math.log(s) / s);
        this.futureGaussian = v2 * multiplier;
        this.hasFutureGaussian = true;
        return v1 * multiplier;
    }

    public int nextGaussianInt(int bound) {
        return this.nextInt(bound) - this.nextInt(bound);
    }

    public float nextGaussianFloat() {
        return this.nextFloat() - this.nextFloat();
    }

    public Vector3 nextVector3() {
        float x = this.nextFloat();
        float y = this.nextFloat();
        float z = this.nextFloat();
        return new Vector3(x, y, z);
    }

    public Vector3 nextGaussianVector3() {
        float x = (float) nextGaussian();
        float y = (float) nextGaussian();
        float z = (float) nextGaussian();
        return new Vector3(x, y, z);
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

    private double genRandReal2() {
        return Integer.toUnsignedLong(genRandInt32()) * TWO_POW_M32;
    }
}
