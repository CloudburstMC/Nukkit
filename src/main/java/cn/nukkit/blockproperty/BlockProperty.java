package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigInteger;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public abstract class BlockProperty<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -2594821043880025191L;
    
    private final int bitSize;
    private final String name;
    private final String persistenceName;
    private final boolean exportedToItem;

    /**
     * @throws IllegalArgumentException If the bit size is negative
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockProperty(String name, boolean exportedToItem, int bitSize, String persistenceName) {
        Preconditions.checkArgument(bitSize > 0, "Bit size (%s) must be positive", bitSize);
        this.bitSize = bitSize;
        this.name = name;
        this.persistenceName = persistenceName;
        this.exportedToItem = exportedToItem;
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

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    private int computeValueMask(int bitOffset) {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset);
        
        int maskBits = bitSize + bitOffset;
        Preconditions.checkArgument(0 < maskBits && maskBits <= 32, "The bit offset %s plus the bit size %s causes memory overflow (32 bits)", bitOffset, bitSize);
        
        int rightMask = computeRightMask(bitOffset);
        int leftMask = -1 << maskBits;
        return ~rightMask & ~leftMask;
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    private long computeBigValueMask(int bitOffset) {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset);
        
        int maskBits = bitSize + bitOffset;
        Preconditions.checkArgument(0 < maskBits && maskBits <= 64, "The bit offset %s plus the bit size %s causes memory overflow (64 bits)", bitOffset, bitSize);
        
        long rightMask = computeBigRightMask(bitOffset);
        long leftMask = -1L << maskBits;
        return ~rightMask & ~leftMask;
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    private BigInteger computeHugeValueMask(int bitOffset) {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset);

        int maskBits = bitSize + bitOffset;
        Preconditions.checkArgument(0 < maskBits, "The bit offset %s plus the bit size %s causes memory overflow (huge)", bitOffset, bitSize);
        
        BigInteger rightMask = computeHugeRightMask(bitOffset);
        BigInteger leftMask = BigInteger.valueOf(-1).shiftLeft(maskBits);
        
        return rightMask.not().andNot(leftMask);
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by this property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int setValue(int currentMeta, int bitOffset, @Nullable T newValue) {
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

    /**
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by this property
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long setValue(long currentBigMeta, int bitOffset, @Nullable T newValue) {
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

    /**
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by this property
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BigInteger setValue(BigInteger currentHugeMeta, int bitOffset, @Nullable T newValue) {
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

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final int getMetaFromInt(int currentMeta, int bitOffset) {
        return (currentMeta & computeValueMask(bitOffset)) >>> bitOffset;
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final int getMetaFromLong(long currentMeta, int bitOffset) {
        return (int) ((currentMeta & computeBigValueMask(bitOffset)) >>> bitOffset);
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final int getMetaFromBigInt(BigInteger currentMeta, int bitOffset) {
        return currentMeta.and(computeHugeValueMask(bitOffset)).shiftRight(bitOffset).intValue();
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public T getValue(int currentMeta, int bitOffset) {
        int meta = getMetaFromInt(currentMeta, bitOffset);
        try {
            return getValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeValueMask(bitOffset), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public T getValue(long currentBigMeta, int bitOffset) {
        int meta = getMetaFromLong(currentBigMeta, bitOffset);
        try {
            return getValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentBigMeta, currentBigMeta & computeBigValueMask(bitOffset), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public T getValue(BigInteger currentHugeMeta, int bitOffset) {
        int meta = getMetaFromBigInt(currentHugeMeta, bitOffset);
        try {
            return getValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentHugeMeta, currentHugeMeta.and(computeHugeValueMask(bitOffset)), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(int currentMeta, int bitOffset) {
        int meta = getMetaFromInt(currentMeta, bitOffset);
        try {
            return getIntValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeValueMask(bitOffset), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(long currentMeta, int bitOffset) {
        int meta = getMetaFromLong(currentMeta, bitOffset);
        try {
            return getIntValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeBigValueMask(bitOffset), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(BigInteger currentMeta, int bitOffset) {
        int meta = getMetaFromBigInt(currentMeta, bitOffset);
        try {
            return getIntValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta.and(computeHugeValueMask(bitOffset)), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getPersistenceValue(int currentMeta, int bitOffset) {
        int meta = getMetaFromInt(currentMeta, bitOffset);
        try {
            return getPersistenceValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeValueMask(bitOffset), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getPersistenceValue(long currentMeta, int bitOffset) {
        int meta = getMetaFromLong(currentMeta, bitOffset);
        try {
            return getPersistenceValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeBigValueMask(bitOffset), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getPersistenceValue(BigInteger currentMeta, int bitOffset) {
        int meta = getMetaFromBigInt(currentMeta, bitOffset);
        try {
            return getPersistenceValueForMeta(meta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta.and(computeHugeValueMask(bitOffset)), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyValueException If the value is invalid for this property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getMetaForValue(@Nullable T value);

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public abstract T getValueForMeta(int meta);

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getIntValueForMeta(int meta);

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract String getPersistenceValueForMeta(int meta);

    /**
     * @throws RuntimeException Any runtime exception to indicate an invalid value
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected void validateDirectly(@Nullable T value) {
        // Does nothing by default
    }

    /**
     * @throws RuntimeException Any runtime exception to indicate an invalid meta
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected abstract void validateMetaDirectly(int meta);

    /**
     * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final void validateMeta(int meta, int offset) {
        int propMeta = getMetaFromInt(meta, offset);
        try {
            validateMetaDirectly(propMeta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta & computeValueMask(offset), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final void validateMeta(long meta, int offset) {
        int propMeta = getMetaFromLong(meta, offset);
        try {
            validateMetaDirectly(propMeta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta & computeBigValueMask(offset), e);
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final void validateMeta(BigInteger meta, int offset) {
        int propMeta = getMetaFromBigInt(meta, offset);
        try {
            validateMetaDirectly(propMeta);
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta.and(computeHugeRightMask(offset)), e);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBitSize() {
        return bitSize;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getName() {
        return name;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getPersistenceName() {
        return persistenceName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public abstract Class<T> getValueClass();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isExportedToItem() {
        return exportedToItem;
    }

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
