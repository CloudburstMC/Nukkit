package cn.nukkit.server.util.bitset;

public interface BitSet {

    void set(int index, boolean value);

    boolean get(int index);

    long getAsLong();

    int getAsInt();

    short getAsShort();

    byte getAsByte();
}
