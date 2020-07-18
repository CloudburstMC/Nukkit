package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public abstract class BlockProperty<T> {
    private final int bitSize;
    private final String name;
    private final String persistenceName;
    private final boolean exportedToItem;

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
        return BigInteger.ONE.shiftLeft(bitSize).subtract(BigInteger.ONE).shiftLeft(bitOffset);
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
        BigInteger leftMask = BigInteger.ONE.negate().shiftLeft(maskBits);
        
        return rightMask.not().andNot(leftMask);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int setValue(int currentMeta, int bitOffset, @Nullable T newValue) {
        try {
            int mask = computeValueMask(bitOffset);
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
                suppressed = new InvalidBlockPropertyMetaException(this, currentMeta, currentMeta & computeValueMask(bitOffset), e2);
            }
            InvalidBlockPropertyValueException toThrow = new InvalidBlockPropertyValueException(this, oldValue, newValue, e);
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed);
            }
            throw toThrow;
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long setValue(long currentBigMeta, int bitOffset, @Nullable T newValue) {
        try {
            long mask = computeBigValueMask(bitOffset);
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
                suppressed = new InvalidBlockPropertyMetaException(this, currentBigMeta, currentBigMeta & computeBigValueMask(bitOffset), e2);
            }
            InvalidBlockPropertyValueException toThrow = new InvalidBlockPropertyValueException(this, oldValue, newValue, e);
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed);
            }
            throw toThrow;
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BigInteger setValue(BigInteger currentHugeMeta, int bitOffset, @Nullable T newValue) {
        try {
            BigInteger mask = computeHugeValueMask(bitOffset);
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
                suppressed = new InvalidBlockPropertyMetaException(this, currentHugeMeta, currentHugeMeta.and(computeHugeValueMask(bitOffset)), e2);
            }
            InvalidBlockPropertyValueException toThrow = new InvalidBlockPropertyValueException(this, oldValue, newValue, e);
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed);
            }
            throw toThrow;
        }
    }
    
    private int getMeta(int currentMeta, int bitOffset) {
        return (currentMeta & computeValueMask(bitOffset)) >>> bitOffset;
    }
    
    private int getMetaFromBig(long currentMeta, int bitOffset) {
        return (int) ((currentMeta & computeBigValueMask(bitOffset)) >>> bitOffset);
    }
    
    private int getMetaFromHuge(BigInteger currentMeta, int bitOffset) {
        return currentMeta.and(computeHugeValueMask(bitOffset)).shiftRight(bitOffset).intValue();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public T getValue(int currentMeta, int bitOffset) {
        return getValueForMeta(getMeta(currentMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public T getValue(long currentBigMeta, int bitOffset) {
        return getValueForMeta(getMetaFromBig(currentBigMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public T getValue(BigInteger currentHugeMeta, int bitOffset) {
        return getValueForMeta(getMetaFromHuge(currentHugeMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(int currentMeta, int bitOffset) {
        return getIntValueForMeta(getMeta(currentMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(long currentMeta, int bitOffset) {
        return getIntValueForMeta(getMetaFromBig(currentMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(BigInteger currentMeta, int bitOffset) {
        return getIntValueForMeta(getMetaFromHuge(currentMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getPersistenceValue(int currentMeta, int bitOffset) {
        return getPersistenceValueForMeta(getMeta(currentMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getPersistenceValue(long currentMeta, int bitOffset) {
        return getPersistenceValueForMeta(getMetaFromBig(currentMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getPersistenceValue(BigInteger currentMeta, int bitOffset) {
        return getPersistenceValueForMeta(getMetaFromHuge(currentMeta, bitOffset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getMetaForValue(@Nullable T value);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public abstract T getValueForMeta(int meta);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getIntValueForMeta(int meta);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract String getPersistenceValueForMeta(int meta);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected void validate(@Nullable T value) {
        // Does nothing by default
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected abstract void validateMeta(int meta);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final void validateMeta(int meta, int offset) {
        try {
            validateMeta(getMeta(meta, offset));
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta & computeValueMask(offset), e);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final void validateMeta(long meta, int offset) {
        try {
            validateMeta(getMetaFromBig(meta, offset));
        } catch (Exception e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta & computeBigValueMask(offset), e);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final void validateMeta(BigInteger meta, int offset) {
        try {
            validateMeta(getMetaFromHuge(meta, offset));
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
    public String getName() {
        return name;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public String getPersistenceName() {
        return persistenceName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract Class<T> getValueClass();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isExportedToItem() {
        return exportedToItem;
    }
}
