package cn.nukkit.customblock.properties;

import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyValueException;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.math.BigInteger;

public abstract class BlockProperty<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -2594821043880025191L;

    private final String name;
    private final boolean exportedToItem;
    private final String persistenceName;
    private final int bitSize;

    public BlockProperty(String name, boolean exportedToItem, String persistenceName, int bitSize) {
        Preconditions.checkArgument(bitSize > 0, "Bit size (%s) must be positive", bitSize);
        this.name = name;
        this.exportedToItem = exportedToItem;
        this.persistenceName = persistenceName;
        this.bitSize = bitSize;
    }

    public abstract int getMetaForValue(T value);
    public abstract T getValueForMeta(int meta);
    public abstract int getIntValueForMeta(int meta);

    public abstract Serializable getPersistenceValueForMeta(int meta);
    public abstract int getMetaForPersistenceValue(String persistenceValue);

    public abstract Class<T> getValueClass();

    public abstract boolean isDefaultValue(T value);
    public abstract T getDefaultValue();
    public abstract BlockProperty<T> exportingToItems(boolean exportedToItem);

    public int setValue(int currentMeta, int bitOffset, T newValue) {
        int mask = computeValueMask(bitOffset);
        try {
            int value = getMetaForValue(newValue) << bitOffset;

            if ((value & ~mask) != 0) {
                throw new IllegalStateException("Attempted to set a value which overflows the size of " + bitSize + " bits. Current:" + currentMeta + ", offset:" + bitOffset + ", meta:" + value + ", value:" + newValue);
            }

            return currentMeta & ~mask | (value & mask);
        } catch (Exception e) {
            T oldValue = null;
            InvalidBlockPropertyMetaException suppressed = null;
            try {
                oldValue = getValue(currentMeta, bitOffset);
            } catch (Exception e2) {
                suppressed = new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & mask, e2);
            }
            InvalidBlockPropertyValueException toThrow = new InvalidBlockPropertyValueException(this, oldValue, newValue, e);
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed);
            }
            throw toThrow;
        }
    }

    public long setValue(long currentBigMeta, int bitOffset, T newValue) {
        long mask = computeBigValueMask(bitOffset);
        try {
            long value = getMetaForValue(newValue) << bitOffset;

            if ((value & ~mask) != 0L) {
                throw new IllegalStateException("Attempted to set a value which overflows the size of " + bitSize + " bits. Current:" + currentBigMeta + ", offset:" + bitOffset + ", meta:" + value + ", value:" + newValue);
            }

            return currentBigMeta & ~mask | (value & mask);
        } catch (Exception e) {
            T oldValue = null;
            InvalidBlockPropertyMetaException suppressed = null;
            try {
                oldValue = getValue(currentBigMeta, bitOffset);
            } catch (Exception e2) {
                suppressed = new InvalidBlockPropertyMetaException(this, currentBigMeta, currentBigMeta & mask, e2);
            }
            InvalidBlockPropertyValueException toThrow = new InvalidBlockPropertyValueException(this, oldValue, newValue, e);
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed);
            }
            throw toThrow;
        }
    }

    public BigInteger setValue(BigInteger currentHugeMeta, int bitOffset, T newValue) {
        BigInteger mask = computeHugeValueMask(bitOffset);
        try {
            BigInteger value = BigInteger.valueOf(getMetaForValue(newValue)).shiftLeft(bitOffset);

            if (!value.andNot(mask).equals(BigInteger.ZERO)) {
                throw new IllegalStateException("Attempted to set a value which overflows the size of " + bitSize + " bits. Current:" + currentHugeMeta + ", offset:" + bitOffset + ", meta:" + value + ", value:" + newValue);
            }

            return currentHugeMeta.andNot(mask).or(value.and(mask));
        } catch (Exception e) {
            T oldValue = null;
            InvalidBlockPropertyMetaException suppressed = null;
            try {
                oldValue = getValue(currentHugeMeta, bitOffset);
            } catch (Exception e2) {
                suppressed = new InvalidBlockPropertyMetaException(this, currentHugeMeta, currentHugeMeta.and(mask), e2);
            }
            InvalidBlockPropertyValueException toThrow = new InvalidBlockPropertyValueException(this, oldValue, newValue, e);
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed);
            }
            throw toThrow;
        }
    }

    public T getValue(int currentMeta, int bitOffset) {
        int meta = getMetaFromInt(currentMeta, bitOffset);
        try {
            return getValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeValueMask(bitOffset), e);
        }
    }

    public T getValue(long currentBigMeta, int bitOffset) {
        int meta = getMetaFromLong(currentBigMeta, bitOffset);
        try {
            return getValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentBigMeta, currentBigMeta & computeBigValueMask(bitOffset), e);
        }
    }

    public T getValue(BigInteger currentHugeMeta, int bitOffset) {
        int meta = getMetaFromBigInt(currentHugeMeta, bitOffset);
        try {
            return getValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentHugeMeta, currentHugeMeta.and(computeHugeValueMask(bitOffset)), e);
        }
    }

    public int getIntValue(int currentMeta, int bitOffset) {
        int meta = getMetaFromInt(currentMeta, bitOffset);
        try {
            return getIntValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeValueMask(bitOffset), e);
        }
    }

    public int getIntValue(long currentMeta, int bitOffset) {
        int meta = getMetaFromLong(currentMeta, bitOffset);
        try {
            return getIntValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeBigValueMask(bitOffset), e);
        }
    }

    public int getIntValue(BigInteger currentMeta, int bitOffset) {
        int meta = getMetaFromBigInt(currentMeta, bitOffset);
        try {
            return getIntValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta.and(computeHugeValueMask(bitOffset)), e);
        }
    }

    public Serializable getPersistenceValue(int currentMeta, int bitOffset) {
        int meta = getMetaFromInt(currentMeta, bitOffset);
        try {
            return this.getPersistenceValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeValueMask(bitOffset), e);
        }
    }

    public Serializable getPersistenceValue(long currentMeta, int bitOffset) {
        int meta = getMetaFromLong(currentMeta, bitOffset);
        try {
            return this.getPersistenceValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeBigValueMask(bitOffset), e);
        }
    }

    public Serializable getPersistenceValue(BigInteger currentMeta, int bitOffset) {
        int meta = getMetaFromBigInt(currentMeta, bitOffset);
        try {
            return this.getPersistenceValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta.and(computeHugeValueMask(bitOffset)), e);
        }
    }


    private int computeRightMask(int bitOffset) {
        return bitOffset == 0? 0 : -1 >>> (32 - bitOffset);
    }

    private long computeBigRightMask(int bitOffset) {
        return bitOffset == 0L? 0L : -1L >>> (64 - bitOffset);
    }

    private BigInteger computeHugeRightMask(int bitOffset) {
        return BigInteger.ONE.shiftLeft(bitOffset).subtract(BigInteger.ONE);
    }

    private int computeValueMask(int bitOffset) {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset);

        int maskBits = bitSize + bitOffset;
        Preconditions.checkArgument(0 < maskBits && maskBits <= 32, "The bit offset %s plus the bit size %s causes memory overflow (32 bits)", bitOffset, bitSize);

        int rightMask = computeRightMask(bitOffset);
        int leftMask = -1 << maskBits;
        return ~rightMask & ~leftMask;
    }

    private long computeBigValueMask(int bitOffset) {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset);

        int maskBits = bitSize + bitOffset;
        Preconditions.checkArgument(0 < maskBits && maskBits <= 64, "The bit offset %s plus the bit size %s causes memory overflow (64 bits)", bitOffset, bitSize);

        long rightMask = computeBigRightMask(bitOffset);
        long leftMask = -1L << maskBits;
        return ~rightMask & ~leftMask;
    }

    private BigInteger computeHugeValueMask(int bitOffset) {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset);

        int maskBits = bitSize + bitOffset;
        Preconditions.checkArgument(0 < maskBits, "The bit offset %s plus the bit size %s causes memory overflow (huge)", bitOffset, bitSize);

        BigInteger rightMask = computeHugeRightMask(bitOffset);
        BigInteger leftMask = BigInteger.valueOf(-1).shiftLeft(maskBits);

        return rightMask.not().andNot(leftMask);
    }

    public final int getMetaFromInt(int currentMeta, int bitOffset) {
        return (currentMeta & computeValueMask(bitOffset)) >>> bitOffset;
    }

    public final int getMetaFromLong(long currentMeta, int bitOffset) {
        return (int) ((currentMeta & computeBigValueMask(bitOffset)) >>> bitOffset);
    }

    public final int getMetaFromBigInt(BigInteger currentMeta, int bitOffset) {
        return currentMeta.and(computeHugeValueMask(bitOffset)).shiftRight(bitOffset).intValue();
    }

    protected void validateDirectly(T value) {
        // Does nothing by default
    }

    protected abstract void validateMetaDirectly(int meta);

    public final void validateMeta(int meta, int offset) {
        int propMeta = getMetaFromInt(meta, offset);
        try {
            validateMetaDirectly(propMeta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta & computeValueMask(offset), e);
        }
    }

    public final void validateMeta(long meta, int offset) {
        int propMeta = getMetaFromLong(meta, offset);
        try {
            validateMetaDirectly(propMeta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta & computeBigValueMask(offset), e);
        }
    }

    public final void validateMeta(BigInteger meta, int offset) {
        int propMeta = getMetaFromBigInt(meta, offset);
        try {
            validateMetaDirectly(propMeta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta.and(computeHugeRightMask(offset)), e);
        }
    }

    public boolean isDefaultIntValue(int value) {
        return value == getDefaultIntValue();
    }

    public boolean isDefaultBooleanValue(boolean value) {
        return value == getDefaultBooleanValue();
    }

    public int getDefaultIntValue() {
        return 0;
    }

    public boolean getDefaultBooleanValue() {
        return false;
    }

    public int getBitSize() {
        return this.bitSize;
    }

    public String getName() {
        return this.name;
    }

    public String getPersistenceName() {
        return this.persistenceName;
    }

    public boolean isExportedToItem() {
        return this.exportedToItem;
    }

    public abstract BlockProperty<T> copy();

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" +
                "name='" + name + '\'' +
                ", bitSize=" + bitSize +
                ", exportedToItem=" + exportedToItem +
                ", persistenceName='" + persistenceName + '\'' +
                '}';
    }
}
