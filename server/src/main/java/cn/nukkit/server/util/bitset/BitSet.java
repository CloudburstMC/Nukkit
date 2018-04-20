package cn.nukkit.server.util.bitset;

public interface BitSet {

    void flip(int index);

    void set(int index, boolean value);

    boolean get(int index);

    long[] getLongs();

    int[] getInts();

    short[] getShorts();

    byte[] getBytes();

    /**
     * Set all bits to 0
     */
    void clear();
}
