package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.NukkitMath;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class UnsignedIntBlockProperty extends BlockProperty<Integer> {
    private final int defaultMeta;
    private final long minValue;
    private final long maxValue;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int defaultValue, int bitSize, String persistenceName) {
        super(name, exportedToItem, bitSize, persistenceName);
        long unsignedMinValue = removeSign(minValue);
        long unsignedMaxValue = removeSign(maxValue);
        long delta = unsignedMaxValue - unsignedMinValue;
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", unsignedMinValue, unsignedMaxValue);
        
        long mask = removeSign(-1 >>> (32 - bitSize));
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %s bits", unsignedMinValue, unsignedMaxValue, bitSize);
        
        long unsignedDefault = removeSign(defaultValue);
        Preconditions.checkArgument(unsignedMinValue <= unsignedDefault && unsignedDefault <= unsignedMaxValue, "The default value %s is not inside the %s .. %s range", unsignedDefault, unsignedMinValue, unsignedMaxValue);
        this.minValue = unsignedMinValue;
        this.maxValue = unsignedMaxValue;
        this.defaultMeta = getMetaForValue(defaultValue);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int defaultValue, int bitSize) {
        this(name, exportedToItem, maxValue, minValue, defaultValue, bitSize, name);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int defaultValue) {
        this(name, exportedToItem, maxValue, minValue, defaultValue, NukkitMath.bitLength(maxValue - minValue));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue) {
        this(name, exportedToItem, maxValue, minValue, minValue);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue) {
        this(name, exportedToItem, maxValue, 0);
    }
    
    private static long removeSign(int value) {
        return (long)value & 0xFFFFFFFFL;
    }

    @Override
    public int getMetaForValue(@Nullable Integer value) {
        if (value == null) {
            return defaultMeta;
        }
        long unsigned = removeSign(value);
        validate(unsigned);
        return (int) (unsigned - minValue);
    }

    @Nonnull
    @Override
    public Integer getValueForMeta(int meta) {
        return getIntValueForMeta(meta);
    }

    @Override
    public int getIntValueForMeta(int meta) {
        return (int) (minValue + meta);
    }

    @Override
    public String getPersistenceValueForMeta(int meta) {
        return String.valueOf(getIntValueForMeta(meta));
    }

    @Override
    protected void validate(@Nullable Integer value) {
        if (value == null) {
            return;
        }
        validate(removeSign(value));
    }
    
    private void validate(long unsigned) {
        Preconditions.checkArgument(unsigned >= minValue, "New value (%s) must be higher or equals to %s", unsigned, minValue);
        Preconditions.checkArgument(maxValue >= unsigned, "New value (%s) must be less or equals to %s", unsigned, maxValue);
    }

    @Override
    protected void validateMeta(int meta) {
        long max = maxValue - minValue;
        Preconditions.checkArgument(0 <= meta && meta <= max, "The meta %s is outside the range of 0 .. ", meta, max);
    }

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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getDefaultValue() {
        return getValueForMeta(defaultMeta);
    }
}
