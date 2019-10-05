package cn.nukkit.utils;


import com.google.common.base.Preconditions;

import java.util.Arrays;

public class NibbleArray implements Cloneable {
    private final int size;
    private final byte[] data;

    public NibbleArray(int size) {
        Preconditions.checkArgument(size > 1 && (size & 1) == 0, "Size must be divisible by 2");
        this.size = size;
        this.data = new byte[size >> 1];
    }

    public NibbleArray(byte[] array) {
        this.size = array.length << 1;
        this.data = array;
    }

    public byte get(int index) {
        Preconditions.checkElementIndex(index, this.size);
        byte val = this.data[index >> 1];
        if ((index & 1) == 0) {
            return (byte) (val & 0x0f);
        } else {
            return (byte) ((val & 0xf0) >>> 4);
        }
    }

    public void set(int index, byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16,
                "Nibbles must have a value between 0 and 15.");
        Preconditions.checkElementIndex(index, this.size);
        int half = index >> 1;
        byte previous = this.data[half];
        if ((index & 1) == 0) {
            this.data[half] = (byte) (previous & 0xf0 | value);
        } else {
            this.data[half] = (byte) (previous & 0x0f | value << 4);
        }
    }

    public void fill(byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16,
                "Nibbles must have a value between 0 and 15.");
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (byte) ((value << 4) | value);
        }
    }

    public void copyFrom(byte[] bytes) {
        Preconditions.checkNotNull(bytes, "bytes");
        Preconditions.checkArgument(bytes.length == this.data.length,
                "length of provided byte array is %s but expected %s", bytes.length, this.data.length);
        System.arraycopy(bytes, 0, this.data, 0, this.data.length);
    }

    public void copyFrom(NibbleArray array) {
        Preconditions.checkNotNull(array, "array");
        copyFrom(array.data);
    }

    public byte[] getData() {
        return data;
    }

    public NibbleArray copy() {
        return new NibbleArray(Arrays.copyOf(this.data, this.data.length));
    }
}
