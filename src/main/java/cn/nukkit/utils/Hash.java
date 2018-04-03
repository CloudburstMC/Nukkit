package cn.nukkit.utils;

public class Hash {
    public static long hashBlock(int x, int y, int z) {
        return y + (((long) x & 0x3FFFFFF) << 8) + (((long) z & 0x3FFFFFF) << 34);
    }


    public static final int hashBlockX(long triple) {
        return (int) ((((triple >> 8) & 0x3FFFFFF) << 38) >> 38);
    }

    public static final int hashBlockY(long triple) {
        return (int) (triple & 0xFF);
    }

    public static final int hashBlockZ(long triple) {
        return (int) ((((triple >> 34) & 0x3FFFFFF) << 38) >> 38);
    }
}
