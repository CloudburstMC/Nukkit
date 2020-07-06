package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
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
    public UnsignedIntBlockProperty(String name, int maxValue, int minValue, int defaultValue, int bitSize, String persistenceName) {
        super(name, bitSize, persistenceName);
        long unsignedMinValue = removeSign(minValue);
        long unsignedMaxValue = removeSign(maxValue);
        long delta = unsignedMaxValue - unsignedMinValue;
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", minValue, maxValue);
        
        long mask = -1 >>> (32 - bitSize);
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %d bits", minValue, maxValue, bitSize);

        Preconditions.checkArgument(minValue <= defaultValue && defaultValue <= maxValue, "The default value %s is not inside the %s .. %s range", defaultValue, minValue, maxValue);
        this.minValue = unsignedMinValue;
        this.maxValue = unsignedMaxValue;
        this.defaultMeta = getMetaForValue(defaultValue);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, int maxValue, int minValue, int defaultValue, int bitSize) {
        this(name, maxValue, minValue, defaultValue, bitSize, name);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, int maxValue, int minValue, int defaultValue) {
        this(name, maxValue, minValue, defaultValue, calculateBitSize(maxValue - minValue));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, int maxValue, int minValue) {
        this(name, minValue, maxValue, minValue);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public UnsignedIntBlockProperty(String name, int maxValue) {
        this(name, maxValue, 0);
    }
    
    private long removeSign(int value) {
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
    public void validate(@Nullable Integer value) {
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
    public void validateMeta(int meta) {
        long max = maxValue - minValue;
        Preconditions.checkArgument(0 >= meta && meta >= max, "The meta %s is outside the range of 0 .. ", meta, max);
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
