package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyPersistenceValueException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.math.NukkitMath;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class UnsignedIntBlockProperty extends BlockProperty<Integer> {
    private static final long serialVersionUID = 7896101036099245755L;
    
    private final long minValue;
    private final long maxValue;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int bitSize, String persistenceName) {
        super(name, exportedToItem, bitSize, persistenceName);
        long unsignedMinValue = removeSign(minValue);
        long unsignedMaxValue = removeSign(maxValue);
        long delta = unsignedMaxValue - unsignedMinValue;
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", unsignedMinValue, unsignedMaxValue);
        
        long mask = removeSign(-1 >>> (32 - bitSize));
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %s bits", unsignedMinValue, unsignedMaxValue, bitSize);
        
        this.minValue = unsignedMinValue;
        this.maxValue = unsignedMaxValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int bitSize) {
        this(name, exportedToItem, maxValue, minValue, bitSize, name);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue) {
        this(name, exportedToItem, maxValue, minValue, NukkitMath.bitLength(maxValue - minValue));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue) {
        this(name, exportedToItem, maxValue, 0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public UnsignedIntBlockProperty copy() {
        return new UnsignedIntBlockProperty(getName(), isExportedToItem(), (int)getMaxValue(), (int)getMinValue(), getBitSize(), getPersistenceName());
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public UnsignedIntBlockProperty exportingToItems(boolean exportedToItem) {
        return new UnsignedIntBlockProperty(getName(), exportedToItem, (int)getMaxValue(), (int)getMinValue(), getBitSize(), getPersistenceName());
    }

    private static long removeSign(int value) {
        return (long)value & 0xFFFFFFFFL;
    }
    
    private static int addSign(long value) {
        return (int)(value & 0xFFFFFFFFL);
    }

    @Override
    public int getMetaForValue(@Nullable Integer value) {
        if (value == null) {
            return 0;
        }
        long unsigned = removeSign(value);
        try {
            validateDirectly(unsigned);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyValueException(this, null, value, e);
        }
        return (int) (unsigned - minValue);
    }

    @Nonnull
    @Override
    public Integer getValueForMeta(int meta) {
        return getIntValueForMeta(meta);
    }

    @Override
    public int getIntValueForMeta(int meta) {
        try {
            validateMetaDirectly(meta);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, e);
        }
        return (int) (minValue + meta);
    }

    @Override
    public String getPersistenceValueForMeta(int meta) {
        return String.valueOf(removeSign(getIntValueForMeta(meta)));
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getMetaForPersistenceValue(@Nonnull String persistenceValue) {
        try {
            return getMetaForValue(addSign(Long.parseLong(persistenceValue)));
        } catch (NumberFormatException | InvalidBlockPropertyValueException e) {
            throw new InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, e);
        }
    }

    @Override
    protected void validateDirectly(@Nullable Integer value) {
        if (value == null) {
            return;
        }
        validateDirectly(removeSign(value));
    }

    /**
     * @throws RuntimeException Any runtime exception to indicate an invalid value
     */
    private void validateDirectly(long unsigned) {
        Preconditions.checkArgument(unsigned >= minValue, "New value (%s) must be higher or equals to %s", unsigned, minValue);
        Preconditions.checkArgument(maxValue >= unsigned, "New value (%s) must be less or equals to %s", unsigned, maxValue);
    }

    @Override
    protected void validateMetaDirectly(int meta) {
        long max = maxValue - minValue;
        Preconditions.checkArgument(0 <= meta && meta <= max, "The meta %s is outside the range of 0 .. ", meta, max);
    }

    @Nonnull
    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long getMaxValue() {
        return maxValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long getMinValue() {
        return minValue;
    }

    @Nonnull
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Integer getDefaultValue() {
        return (int)minValue;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultValue(@Nullable Integer value) {
        return value == null || removeSign(value)==minValue;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultIntValue(int value) {
        return removeSign(value)==minValue;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getDefaultIntValue() {
        return (int)minValue;
    }
}
