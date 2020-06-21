package cn.nukkit.level.format.anvil.palette;

import java.util.Arrays;

/**
 * @author https://github.com/boy0001/
 */
public class IntPalette {

    private static final int[] INT0 = new int[0];

    private int[] keys = IntPalette.INT0;

    private int lastIndex = Integer.MIN_VALUE;

    public void add(final int key) {
        this.keys = this.insert(key);
        this.lastIndex = Integer.MIN_VALUE;
    }

    public int getKey(final int index) {
        return this.keys[index];
    }

    public int getValue(final int key) {
        final int lastTmp = this.lastIndex;
        final boolean hasLast = lastTmp != Integer.MIN_VALUE;
        final int index;
        if (hasLast) {
            final int lastKey = this.keys[lastTmp];
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
            return this.lastIndex = Integer.MIN_VALUE;
        } else {
            return this.lastIndex = index;
        }
    }

    public int length() {
        return this.keys.length;
    }

    @Override
    public IntPalette clone() {
        final IntPalette p = new IntPalette();
        p.keys = this.keys != IntPalette.INT0 ? this.keys.clone() : IntPalette.INT0;
        p.lastIndex = this.lastIndex;
        return p;
    }

    protected void set(final int[] keys) {
        this.keys = keys;
        this.lastIndex = Integer.MIN_VALUE;
    }

    private int[] insert(final int val) {
        this.lastIndex = Integer.MIN_VALUE;
        if (this.keys.length == 0) {
            return new int[]{val};
        } else if (val < this.keys[0]) {
            final int[] s = new int[this.keys.length + 1];
            System.arraycopy(this.keys, 0, s, 1, this.keys.length);
            s[0] = val;
            return s;
        } else if (val > this.keys[this.keys.length - 1]) {
            final int[] s = Arrays.copyOf(this.keys, this.keys.length + 1);
            s[this.keys.length] = val;
            return s;
        }
        final int[] s = Arrays.copyOf(this.keys, this.keys.length + 1);
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

    private int binarySearch0(final int fromIndex, final int toIndex, final int key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            final int mid = low + high >>> 1;
            final int midVal = this.keys[mid];

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
