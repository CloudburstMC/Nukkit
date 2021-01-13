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
public class IntBlockProperty extends BlockProperty<Integer> {
    private static final long serialVersionUID = -2239010977496415152L;
    
    private final int minValue;
    private final int maxValue;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int bitSize, String persistenceName) {
        super(name, exportedToItem, bitSize, persistenceName);
        int delta = maxValue - minValue;
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", minValue, maxValue);
        
        int mask = -1 >>> (32 - bitSize);
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %s bits", minValue, maxValue, bitSize);

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int bitSize) {
        this(name, exportedToItem, maxValue, minValue, bitSize, name);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue) {
        this(name, exportedToItem, maxValue, minValue, NukkitMath.bitLength(maxValue - minValue));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue) {
        this(name, exportedToItem, maxValue, 0);
    }

    @Override
    public int getMetaForValue(@Nullable Integer value) {
        if (value == null) {
            return 0;
        }
        return getMetaForValue(value.intValue());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getMetaForValue(int value) {
        try {
            validateDirectly(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyValueException(this, null, value, e);
        }
        return value - minValue;
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
        return minValue + meta;
    }

    @Override
    public String getPersistenceValueForMeta(int meta) {
        return String.valueOf(getIntValueForMeta(meta));
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getMetaForPersistenceValue(@Nonnull String persistenceValue) {
        try {
            return getMetaForValue(Integer.parseInt(persistenceValue));
        } catch (NumberFormatException | InvalidBlockPropertyValueException e) {
            throw new InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, e);
        }
    }

    @Override
    protected void validateDirectly(@Nullable Integer value) {
        if (value == null) {
            return;
        }
        validateDirectly(value.intValue());
    }
    
    private void validateDirectly(int newValue) {
        Preconditions.checkArgument(newValue >= minValue, "New value (%s) must be higher or equals to %s", newValue, minValue);
        Preconditions.checkArgument(maxValue >= newValue, "New value (%s) must be less or equals to %s", newValue, maxValue);
    }

    @Override
    protected void validateMetaDirectly(int meta) {
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
    @Nonnull
    public Integer getDefaultValue() {
        return minValue;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultIntValue(int value) {
        return minValue == value;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getDefaultIntValue() {
        return minValue;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultValue(@Nullable Integer value) {
        return value == null || minValue == value;
    }

    @Nonnull
    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }
}
