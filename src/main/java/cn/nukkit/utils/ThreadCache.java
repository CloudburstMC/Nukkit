package cn.nukkit.utils;

public class ThreadCache {
    public static void clean() {
        intCache256.clean();
    }

    public static final IterableThreadLocal<int[]> intCache256 = new IterableThreadLocal<int[]>() {
        @Override
        public int[] init() {
            return new int[256];
        }
    };
}
