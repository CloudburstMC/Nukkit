package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.NukkitMath;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class IntBlockProperty extends BlockProperty<Integer> {
    private final int defaultMeta;
    private final int minValue;
    private final int maxValue;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int defaultValue, int bitSize, String persistenceName) {
        super(name, exportedToItem, bitSize, persistenceName);
        int delta = maxValue - minValue;
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", minValue, maxValue);
        
        int mask = -1 >>> (32 - bitSize);
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %s bits", minValue, maxValue, bitSize);

        Preconditions.checkArgument(minValue <= defaultValue && defaultValue <= maxValue, "The default value %s is not inside the %s .. %s range", defaultValue, minValue, maxValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultMeta = getMetaForValue(defaultValue);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int defaultValue, int bitSize) {
        this(name, exportedToItem, maxValue, minValue, defaultValue, bitSize, name);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int defaultValue) {
        this(name, exportedToItem, maxValue, minValue, defaultValue, NukkitMath.bitLength(maxValue - minValue));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue) {
        this(name, exportedToItem, maxValue, minValue, minValue);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue) {
        this(name, exportedToItem, maxValue, 0);
    }

    @Override
    public int getMetaForValue(@Nullable Integer value) {
        if (value == null) {
            return defaultMeta;
        }
        validate(value);
        return value - minValue;
    }

    @Nonnull
    @Override
    public Integer getValueForMeta(int meta) {
        return getIntValueForMeta(meta);
    }

    @Override
    public int getIntValueForMeta(int meta) {
        return minValue + meta;
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
        int newValue = value;
        Preconditions.checkArgument(newValue >= minValue, "New value (%s) must be higher or equals to %s", newValue, minValue);
        Preconditions.checkArgument(maxValue >= newValue, "New value (%s) must be less or equals to %s", newValue, maxValue);
    }

    @Override
    protected void validateMeta(int meta) {
        int max = maxValue - minValue;
        Preconditions.checkArgument(0 <= meta && meta <= max, "The meta %s is outside the range of 0 .. ", meta, max);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getMaxValue() {
        return maxValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getMinValue() {
        return minValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getDefaultValue() {
        return getValueForMeta(defaultMeta);
    }

    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }
}
