package cn.nukkit.level.format.anvil.palette;

import io.netty.util.internal.EmptyArrays;

import java.util.Arrays;

/**
 * @author https://github.com/boy0001/
 */
public class BytePalette {
    private static byte[] BYTE0 = EmptyArrays.EMPTY_BYTES;
    private byte[] keys = BYTE0;
    private byte lastIndex = Byte.MIN_VALUE;

    public void add(byte key) {
        keys = insert(key);
        lastIndex = Byte.MIN_VALUE;
    }

    protected void set(byte[] keys) {
        this.keys = keys;
        lastIndex = Byte.MIN_VALUE;
    }

    private byte[] insert(byte val) {
        lastIndex = Byte.MIN_VALUE;
        if (keys.length == 0) {
            return new byte[] { val };
        }
        else if (val < keys[0]) {
            byte[] s = new byte[keys.length + 1];
            System.arraycopy(keys, 0, s, 1, keys.length);
            s[0] = val;
            return s;
        } else if (val > keys[keys.length - 1]) {
            byte[] s = Arrays.copyOf(keys, keys.length + 1);
            s[keys.length] = val;
            return s;
        }
        byte[] s = Arrays.copyOf(keys, keys.length + 1);
        for (int i = 0; i < s.length; i++) {
            if (keys[i] < val) {
                continue;
            }
            System.arraycopy(keys, i, s, i + 1, s.length - i - 1);
            s[i] = val;
            break;
        }
        return s;
    }

    public byte getKey(int index) {
        return keys[index];
    }

    public byte getValue(byte key) {
        byte lastTmp = lastIndex;
        boolean hasLast = lastTmp != Byte.MIN_VALUE;
        int index;
        if (hasLast) {
            byte lastKey = keys[lastTmp];
            if (lastKey == key) return lastTmp;
            if (lastKey > key) {
                index = binarySearch0(0, lastTmp, key);
            } else {
                index = binarySearch0(lastTmp + 1, keys.length, key);
            }
        } else {
            index = binarySearch0(0, keys.length, key);
        }
        if (index >= keys.length || index < 0) {
            return lastIndex = Byte.MIN_VALUE;
        } else {
            return lastIndex = (byte) index;
        }
    }

    private int binarySearch0(int fromIndex, int toIndex, byte key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            byte midVal = keys[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }
}
