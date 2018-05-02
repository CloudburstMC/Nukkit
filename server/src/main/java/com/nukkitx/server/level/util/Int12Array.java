package com.nukkitx.server.level.util;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;

public class Int12Array {
    private final byte[] words;
    private final int size;

    public Int12Array(@Nonnegative int size) {
        Preconditions.checkArgument(size > 0, "Array size negative or zero");
        this.size = (int) Math.ceil(size * 1.5f);
        this.words = new byte[this.size];
    }

    public short get(int index) {
        int i = index * 3;
        int half = i / 2;
        Preconditions.checkElementIndex(half, words.length * 2);

        byte val1 = words[half];
        byte val2 = words[half + 1];
        if ((i & 1) == 0) {
            return (short) ((val1 & 0x0f) << 8 | val2);
        } else {
            return (short) ((val1 << 4) | (val1 & 0xf0) >>> 4);
        }
    }

    public void set(int index, short value) {
        int i = index * 3;
        int half = i / 2;
        Preconditions.checkElementIndex(half, words.length * 2);

        if ((i & 1) == 0) {
            byte previous = words[half];
            words[half] = (byte) (previous & 0xf0 | value >>> 8);
            words[half + 1] = (byte) (value & 0xff);
        } else {
            byte previous = words[half + 1];
            words[half] = (byte) (value >>> 4);
            words[half + 1] = (byte) (value & 0xf0 | previous & 0x0f);
        }
    }

    public int getSize() {
        return size;
    }
}
