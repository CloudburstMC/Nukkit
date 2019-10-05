package cn.nukkit.utils;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;

public class ThreadCache {
    public static void clean() {
        fbaos.clean();
        intCache256.clean();
    }

    public static final IterableThreadLocal<int[]> intCache256 = new IterableThreadLocal<int[]>() {
        @Override
        public int[] init() {
            return new int[256];
        }
    };

    public static final IterableThreadLocal<FastByteArrayOutputStream> fbaos = new IterableThreadLocal<FastByteArrayOutputStream>() {
        @Override
        public FastByteArrayOutputStream init() {
            return new FastByteArrayOutputStream(1024);
        }
    };
}
