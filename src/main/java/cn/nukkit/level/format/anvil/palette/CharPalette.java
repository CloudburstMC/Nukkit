package cn.nukkit.level.format.anvil.palette;

import java.util.Arrays;

/**
 * @author https://github.com/boy0001/
 */
public class CharPalette {
    private static char[] CHAR0 = new char[0];
    private char[] keys = CHAR0;
    private char lastIndex = Character.MAX_VALUE;

    public void add(char key) {
        keys = insert(key);
        lastIndex = Character.MAX_VALUE;
    }

    protected void set(char[] keys) {
        this.keys = keys;
        lastIndex = Character.MAX_VALUE;
    }

    private char[] insert(char val) {
        if (keys.length == 0) {
            return new char[] { val };
        }
        else if (val < keys[0]) {
            char[] s = new char[keys.length + 1];
            System.arraycopy(keys, 0, s, 1, keys.length);
            s[0] = val;
            return s;
        } else if (val > keys[keys.length - 1]) {
            char[] s = Arrays.copyOf(keys, keys.length + 1);
            s[keys.length] = val;
            return s;
        }
        char[] s = Arrays.copyOf(keys, keys.length + 1);
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

    public char getKey(int index) {
        return keys[index];
    }

    public char getValue(char key) {
        char lastTmp = lastIndex;
        boolean hasLast = lastTmp != Character.MAX_VALUE;
        int index;
        if (hasLast) {
            char lastKey = keys[lastTmp];
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
            return lastTmp = Character.MAX_VALUE;
        } else {
            return lastTmp = (char) index;
        }
    }

    private int binarySearch0(int fromIndex, int toIndex, char key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            char midVal = keys[mid];

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
