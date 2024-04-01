package cn.nukkit.customblock.properties;

import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyPersistenceValueException;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyValueException;
import cn.nukkit.math.NukkitMath;
import com.google.common.base.Preconditions;

import java.io.Serializable;

public class IntBlockProperty extends BlockProperty<Integer> {
    private static final long serialVersionUID = -2239010977496415152L;

    private final int minValue;
    private final int maxValue;

    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int bitSize) {
        this(name, exportedToItem, maxValue, minValue, bitSize, name);
    }

    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue) {
        this(name, exportedToItem, maxValue, minValue, BlockPropertyUtils.bitLength(maxValue - minValue));
    }

    public IntBlockProperty(String name, boolean exportedToItem, int maxValue) {
        this(name, exportedToItem, maxValue, 0);
    }
    
    public IntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int bitSize, String persistenceName) {
        super(name, exportedToItem, persistenceName, bitSize);
        int delta = maxValue - minValue;
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", minValue, maxValue);

        int mask = -1 >>> (32 - bitSize);
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %s bits", minValue, maxValue, bitSize);

        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    @Override
    public int getMetaForValue(Integer value) {
        if (value == null) {
            return 0;
        }
        return this.getMetaForValue(value.intValue());
    }
    
    public int getMetaForValue(int value) {
        try {
            this.validateDirectly(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyValueException(this, null, value, e);
        }
        return value - minValue;
    }

    @Override
    public Integer getValueForMeta(int meta) {
        return this.getIntValueForMeta(meta);
    }

    @Override
    public int getIntValueForMeta(int meta) {
        try {
            this.validateMetaDirectly(meta);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, e);
        }
        return minValue + meta;
    }
    
    @Override
    protected void validateDirectly(Integer value) {
        if (value == null) {
            return;
        }
        this.validateDirectly(value.intValue());
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

    @Override
    public Serializable getPersistenceValueForMeta(int meta) {
        return this.getIntValueForMeta(meta);
    }

    @Override
    public int getMetaForPersistenceValue(String persistenceValue) {
        try {
            return this.getMetaForValue(Integer.parseInt(persistenceValue));
        } catch (NumberFormatException | InvalidBlockPropertyValueException e) {
            throw new InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, e);
        }
    }

    public int clamp(int value) {
        return NukkitMath.clamp(value, this.getMinValue(), this.getMaxValue());
    }

    
    public int getMaxValue() {
        return this.maxValue;
    }

    public int getMinValue() {
        return this.minValue;
    }

    @Override
    public Integer getDefaultValue() {
        return this.minValue;
    }
    
    @Override
    public boolean isDefaultIntValue(int value) {
        return this.minValue == value;
    }
    
    @Override
    public int getDefaultIntValue() {
        return this.minValue;
    }

    @Override
    public boolean isDefaultValue(Integer value) {
        return value == null || this.minValue == value;
    }
    
    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }

    @Override
    public IntBlockProperty exportingToItems(boolean exportedToItem) {
        return new IntBlockProperty(this.getName(), exportedToItem, this.getMaxValue(), this.getMinValue(), this.getBitSize(), this.getPersistenceName());
    }

    @Override
    public IntBlockProperty copy() {
        return new IntBlockProperty(this.getName(), this.isExportedToItem(), this.getMaxValue(), this.getMinValue(), this.getBitSize(), this.getPersistenceName());
    }
}
