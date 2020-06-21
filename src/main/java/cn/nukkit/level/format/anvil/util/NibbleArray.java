package cn.nukkit.level.format.anvil.util;

import com.google.common.base.Preconditions;

public class NibbleArray implements Cloneable {

    private final byte[] data;

    public NibbleArray(final int length) {
        this.data = new byte[length / 2];
    }

    public NibbleArray(final byte[] array) {
        this.data = array;
    }

    public byte get(final int index) {
        Preconditions.checkElementIndex(index, this.data.length * 2);
        final byte val = this.data[index / 2];
        if ((index & 1) == 0) {
            return (byte) (val & 0x0f);
        } else {
            return (byte) ((val & 0xf0) >>> 4);
        }
    }

    public void set(final int index, byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.");
        Preconditions.checkElementIndex(index, this.data.length * 2);
        value &= 0xf;
        final int half = index / 2;
        final byte previous = this.data[half];
        if ((index & 1) == 0) {
            this.data[half] = (byte) (previous & 0xf0 | value);
        } else {
            this.data[half] = (byte) (previous & 0x0f | value << 4);
        }
    }

    public void fill(byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.");
        value &= 0xf;
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (byte) (value << 4 | value);
        }
    }

    public void copyFrom(final byte[] bytes) {
        Preconditions.checkNotNull(bytes, "bytes");
        Preconditions.checkArgument(bytes.length == this.data.length, "length of provided byte array is %s but expected %s", bytes.length,
            this.data.length);
        System.arraycopy(bytes, 0, this.data, 0, this.data.length);
    }

    public void copyFrom(final NibbleArray array) {
        Preconditions.checkNotNull(array, "array");
        this.copyFrom(array.data);
    }

    public byte[] getData() {
        return this.data;
    }

    public NibbleArray copy() {
        return new NibbleArray(this.getData().clone());
    }

}
