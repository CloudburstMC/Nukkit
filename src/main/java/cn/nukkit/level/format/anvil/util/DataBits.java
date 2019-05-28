package cn.nukkit.level.format.anvil.util;

import cn.nukkit.math.MathHelper;
import com.google.common.base.Preconditions;

public class DataBits implements Cloneable {

    private final long[] storage;
    private final int elementBits;
    private final long maxValue;
    private final int size;

    public DataBits(int elementBits, int size) {
        this(elementBits, size, new long[MathHelper.roundUp(size * elementBits, 64) / 64]);
    }

    public DataBits(int elementBits, int size, long[] storage) {
        Preconditions.checkArgument(elementBits >= 1 && elementBits <= 32, "The value " + elementBits + " is not in the specified inclusive range of " + 1 + " to " + 32);
        this.size = size;
        this.elementBits = elementBits;
        this.storage = storage;
        this.maxValue = (1l << elementBits) - 1l;
        int expect = MathHelper.roundUp(size * elementBits, 64) / 64;
        Preconditions.checkArgument(storage.length == expect, "Invalid length given for storage, got: " + storage.length + " but expected: " + expect);
    }

    public int get(int index) {
        Preconditions.checkArgument(index >= 0 && index < this.size, "The value " + index + " is not in the specified inclusive range of " + 0 + " to " + this.size);
        int int_2 = index * this.elementBits;
        int int_3 = int_2 >> 6;
        int int_4 = (index + 1) * this.elementBits - 1 >> 6;
        int int_5 = int_2 ^ int_3 << 6;
        if (int_3 == int_4) {
            return (int) (this.storage[int_3] >>> int_5 & this.maxValue);
        }
        int int_6 = 64 - int_5;
        return (int) ((this.storage[int_3] >>> int_5 | this.storage[int_4] << int_6) & this.maxValue);
    }

    public void set(int index, int value) {
        Preconditions.checkArgument(index >= 0 && index < this.size, "The value " + index + " is not in the specified inclusive range of " + 0 + " to " + this.size);
        Preconditions.checkArgument(value >= 0 && value <= this.maxValue, "The value " + value + " is not in the specified inclusive range of " + 0 + " to " + this.maxValue);
        int int_3 = index * this.elementBits;
        int int_4 = int_3 >> 6;
        int int_5 = (index + 1) * this.elementBits - 1 >> 6;
        int int_6 = int_3 ^ int_4 << 6;
        this.storage[int_4] = ((this.storage[int_4] & ~(this.maxValue << int_6)) | ((long) value & this.maxValue) << int_6);
        if (int_4 != int_5) {
            int int_7 = 64 - int_6;
            int int_8 = this.elementBits - int_7;
            this.storage[int_5] = (this.storage[int_5] >>> int_8 << int_8 | ((long) value & this.maxValue) >> int_7);
        }
    }

    public int getAndSet(int index, int value) {
        Preconditions.checkArgument(index >= 0 && index < this.size, "The value " + index + " is not in the specified inclusive range of " + 0 + " to " + this.size);
        Preconditions.checkArgument(value >= 0 && value <= this.maxValue, "The value " + value + " is not in the specified inclusive range of " + 0 + " to " + this.maxValue);
        int int_3 = index * this.elementBits;
        int int_4 = int_3 >> 6;
        int int_5 = (index + 1) * this.elementBits - 1 >> 6;
        int int_6 = int_3 ^ int_4 << 6;
        int int_7 = 0;
        int_7 |= (int) (this.storage[int_4] >>> int_6 & this.maxValue);
        this.storage[int_4] = ((this.storage[int_4] & ~(this.maxValue << int_6)) | ((long) value & this.maxValue) << int_6);
        if (int_4 != int_5) {
            int int_8 = 64 - int_6;
            int int_9 = this.elementBits - int_8;
            int_7 |= (int) (this.storage[int_5] << int_8 & this.maxValue);
            this.storage[int_5] = (this.storage[int_5] >>> int_9 << int_9 | ((long) value & this.maxValue) >> int_8);
        }
        return int_7;
    }

    public void fill(int value) {
        Preconditions.checkArgument(value >= 0 && value <= this.maxValue, "The value " + value + " is not in the specified inclusive range of " + 0 + " to " + this.maxValue);
        for (int i = 0; i < this.size; i++) {
            this.set(i, value);
        }
    }

    public int getElementBits() {
        return this.elementBits;
    }

    public int getSize() {
        return this.size;
    }

    public long[] getStorage() {
        return this.storage;
    }

    public DataBits copy() {
        return new DataBits(this.getElementBits(), this.getSize(), this.getStorage().clone());
    }
}
