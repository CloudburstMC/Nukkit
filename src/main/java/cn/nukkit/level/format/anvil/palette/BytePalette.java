package cn.nukkit.level.format.anvil.palette;

import java.util.Arrays;

/**
 * @author https://github.com/boy0001/
 */
public class BytePalette {

    private static final byte[] BYTE0 = new byte[0];

    private byte[] keys = BytePalette.BYTE0;

    private byte lastIndex = Byte.MIN_VALUE;

    public void add(final byte key) {
        this.keys = this.insert(key);
        this.lastIndex = Byte.MIN_VALUE;
    }

    public byte getKey(final int index) {
        return this.keys[index];
    }

    public byte getValue(final byte key) {
        final byte lastTmp = this.lastIndex;
        final boolean hasLast = lastTmp != Byte.MIN_VALUE;
        final int index;
        if (hasLast) {
            final byte lastKey = this.keys[lastTmp];
            if (lastKey == key) {
                return lastTmp;
            }
            if (lastKey > key) {
                index = this.binarySearch0(0, lastTmp, key);
            } else {
                index = this.binarySearch0(lastTmp + 1, this.keys.length, key);
            }
        } else {
            index = this.binarySearch0(0, this.keys.length, key);
        }
        if (index >= this.keys.length || index < 0) {
            return this.lastIndex = Byte.MIN_VALUE;
        } else {
            return this.lastIndex = (byte) index;
        }
    }

    protected void set(final byte[] keys) {
        this.keys = keys;
        this.lastIndex = Byte.MIN_VALUE;
    }

    private byte[] insert(final byte val) {
        this.lastIndex = Byte.MIN_VALUE;
        if (this.keys.length == 0) {
            return new byte[]{val};
        } else if (val < this.keys[0]) {
            final byte[] s = new byte[this.keys.length + 1];
            System.arraycopy(this.keys, 0, s, 1, this.keys.length);
            s[0] = val;
            return s;
        } else if (val > this.keys[this.keys.length - 1]) {
            final byte[] s = Arrays.copyOf(this.keys, this.keys.length + 1);
            s[this.keys.length] = val;
            return s;
        }
        final byte[] s = Arrays.copyOf(this.keys, this.keys.length + 1);
        for (int i = 0; i < s.length; i++) {
            if (this.keys[i] < val) {
                continue;
            }
            System.arraycopy(this.keys, i, s, i + 1, s.length - i - 1);
            s[i] = val;
            break;
        }
        return s;
    }

    private int binarySearch0(final int fromIndex, final int toIndex, final byte key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            final int mid = low + high >>> 1;
            final byte midVal = this.keys[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found.
    }

}
