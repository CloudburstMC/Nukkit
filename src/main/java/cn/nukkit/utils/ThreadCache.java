package cn.nukkit.utils;

public class ThreadCache {
    public static final IterableThreadLocal<int[]> intCache256 = new IterableThreadLocal<int[]>() {
        @Override
        public int[] init() {
            return new int[256];
        }
    };

    public static void clean() {
        intCache256.clean();
    }
}
