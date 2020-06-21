package cn.nukkit.level.format.anvil.palette;

import java.util.Arrays;

/**
 * @author https://github.com/boy0001/
 */
public class CharPalette {

    private static final char[] CHAR0 = new char[0];

    private char[] keys = CharPalette.CHAR0;

    private char lastIndex = Character.MAX_VALUE;

    public void add(final char key) {
        this.keys = this.insert(key);
        this.lastIndex = Character.MAX_VALUE;
    }

    public char getKey(final int index) {
        return this.keys[index];
    }

    public char getValue(final char key) {
        final char lastTmp = this.lastIndex;
        final boolean hasLast = lastTmp != Character.MAX_VALUE;
        final int index;
        if (hasLast) {
            final char lastKey = this.keys[lastTmp];
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
            return this.lastIndex = Character.MAX_VALUE;
        } else {
            return this.lastIndex = (char) index;
        }
    }

    protected void set(final char[] keys) {
        this.keys = keys;
        this.lastIndex = Character.MAX_VALUE;
    }

    private char[] insert(final char val) {
        this.lastIndex = Character.MAX_VALUE;
        if (this.keys.length == 0) {
            return new char[]{val};
        } else if (val < this.keys[0]) {
            final char[] s = new char[this.keys.length + 1];
            System.arraycopy(this.keys, 0, s, 1, this.keys.length);
            s[0] = val;
            return s;
        } else if (val > this.keys[this.keys.length - 1]) {
            final char[] s = Arrays.copyOf(this.keys, this.keys.length + 1);
            s[this.keys.length] = val;
            return s;
        }
        final char[] s = Arrays.copyOf(this.keys, this.keys.length + 1);
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

    private int binarySearch0(final int fromIndex, final int toIndex, final char key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            final int mid = low + high >>> 1;
            final char midVal = this.keys[mid];

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
