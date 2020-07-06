package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockProperty<T> {
    private final int bitSize;
    private final String name;
    private final String persistenceName;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockProperty(String name, int bitSize, String persistenceName) {
        Preconditions.checkArgument(bitSize > 0, "Bit size (%s) must be positive", bitSize);
        this.bitSize = bitSize;
        this.name = name;
        this.persistenceName = persistenceName;
    }
    
    private int computeRightMask(int bitOffset) {
        return -1 >>> (32 - bitOffset);
    }
    
    private long computeHyperRightMask(int bitOffset) {
        return -1L >>> (64 - bitOffset);
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
    
    private long computeHyperValueMask(int bitOffset) {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset);
        
        int maskBits = bitSize + bitOffset;
        Preconditions.checkArgument(0 < maskBits && maskBits <= 64, "The bit offset %s plus the bit size %s causes memory overflow (64 bits)", bitOffset, bitSize);
        
        long rightMask = computeHyperRightMask(bitOffset);
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
        int mask = computeValueMask(bitOffset);
        int value = getMetaForValue(newValue);
        
        if ((value & ~mask) != 0) {
            throw new IllegalStateException("Attempted to set a value which overflows the size of "+bitSize+" bits. Current:"+currentMeta+", offset:"+bitOffset+", meta:"+value+", value:"+newValue);
        }
        
        return currentMeta & ~mask | (value << bitOffset & mask);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long setValue(long currentHyperMeta, int bitOffset, @Nullable T newValue) {
        long mask = computeHyperValueMask(bitOffset);
        long value = getMetaForValue(newValue);
        
        if ((value & ~mask) != 0L) {
            throw new IllegalStateException("Attempted to set a value which overflows the size of "+bitSize+" bits. Current:"+currentHyperMeta+", offset:"+bitOffset+", meta:"+value+", value:"+newValue);
        }
        
        return currentHyperMeta & ~mask | (value << bitOffset & mask);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BigInteger setValue(BigInteger currentHugeMeta, int bitOffset, @Nullable T newValue) {
        BigInteger mask = computeHugeValueMask(bitOffset);
        BigInteger value = BigInteger.valueOf(getMetaForValue(newValue));
        
        if (!value.andNot(mask).equals(BigInteger.ZERO)) {
            throw new IllegalStateException("Attempted to set a value which overflows the size of "+bitSize+" bits. Current:"+currentHugeMeta+", offset:"+bitOffset+", meta:"+value+", value:"+newValue);
        }
        
        return currentHugeMeta.andNot(mask).or(value.shiftLeft(bitOffset).and(mask));
    }
    
    private int getMeta(int currentMeta, int bitOffset) {
        return (currentMeta & ~computeRightMask(bitOffset)) >>> bitOffset;
    }
    
    private int getMetaFromHyper(long currentMeta, int bitOffset) {
        return (int) ((currentMeta & ~computeHyperRightMask(bitOffset)) >>> bitOffset);
    }
    
    private int getMetaFromHuge(BigInteger currentMeta, int bitOffset) {
        return currentMeta.andNot(computeHugeRightMask(bitOffset)).shiftRight(bitOffset).intValue();
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
    public T getValue(long currentHyperMeta, int bitOffset) {
        return getValueForMeta(getMetaFromHyper(currentHyperMeta, bitOffset));
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
        return getIntValueForMeta(getMetaFromHyper(currentMeta, bitOffset));
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
        return getPersistenceValueForMeta(getMetaFromHyper(currentMeta, bitOffset));
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
    public void validate(@Nullable T value) {
        // Does nothing by default
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract void validateMeta(int meta);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void validateMeta(int meta, int offset) {
        validateMeta(getMeta(meta, offset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void validateMeta(long meta, int offset) {
        validateMeta(getMetaFromHyper(meta, offset));
    }

    public void validateMeta(BigInteger meta, int offset) {
        validateMeta(getMetaFromHuge(meta, offset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBitSize() {
        return bitSize;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected static int calculateBitSize(int delta) {
        if (delta <= 0) {
            return 1;
        }
        int bits = 1;
        while (delta > 1) {
            delta >>>= 1;
            bits++;
        }
        return bits;
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
}
