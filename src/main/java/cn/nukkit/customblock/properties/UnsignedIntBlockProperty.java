package cn.nukkit.customblock.properties;

import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyPersistenceValueException;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyValueException;
import com.google.common.base.Preconditions;

import java.io.Serializable;

public class UnsignedIntBlockProperty extends BlockProperty<Integer> {
    private static final long serialVersionUID = 7896101036099245755L;

    private final long minValue;
    private final long maxValue;

    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int bitSize) {
        this(name, exportedToItem, maxValue, minValue, bitSize, name);
    }

    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue) {
        this(name, exportedToItem, maxValue, minValue, BlockPropertyUtils.bitLength(maxValue - minValue));
    }

    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue) {
        this(name, exportedToItem, maxValue, 0);
    }

    public UnsignedIntBlockProperty(String name, boolean exportedToItem, int maxValue, int minValue, int bitSize, String persistenceName) {
        super(name, exportedToItem, persistenceName, bitSize);
        long unsignedMinValue = removeSign(minValue);
        long unsignedMaxValue = removeSign(maxValue);
        long delta = unsignedMaxValue - unsignedMinValue;
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", unsignedMinValue, unsignedMaxValue);

        long mask = removeSign(-1 >>> (32 - bitSize));
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %s bits", unsignedMinValue, unsignedMaxValue, bitSize);

        this.minValue = unsignedMinValue;
        this.maxValue = unsignedMaxValue;
    }

    @Override
    public int getMetaForValue(Integer value) {
        if (value == null) {
            return 0;
        }
        
        long unsigned = removeSign(value);
        try {
            this.validateDirectly(unsigned);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyValueException(this, null, value, e);
        }
        return (int) (unsigned - minValue);
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
        return (int) (this.minValue + meta);
    }
    
    @Override
    protected void validateDirectly(Integer value) {
        if (value == null) {
            return;
        }
        this.validateDirectly(removeSign(value));
    }

    private void validateDirectly(long unsigned) {
        Preconditions.checkArgument(unsigned >= this.minValue, "New value (%s) must be higher or equals to %s", unsigned, this.minValue);
        Preconditions.checkArgument(this.maxValue >= unsigned, "New value (%s) must be less or equals to %s", unsigned, this.maxValue);
    }
    
    @Override
    protected void validateMetaDirectly(int meta) {
        long max = this.maxValue - this.minValue;
        Preconditions.checkArgument(0 <= meta && meta <= max, "The meta %s is outside the range of 0 .. ", meta, max);
    }

    public long getMaxValue() {
        return this.maxValue;
    }

    public long getMinValue() {
        return this.minValue;
    }

    @Override
    public Integer getDefaultValue() {
        return (int) this.minValue;
    }

    @Override
    public boolean isDefaultValue(Integer value) {
        return value == null || removeSign(value) == this.minValue;
    }

    @Override
    public boolean isDefaultIntValue(int value) {
        return removeSign(value) == this.minValue;
    }
    
    @Override
    public int getDefaultIntValue() {
        return (int) this.minValue;
    }

    @Override
    public Serializable getPersistenceValueForMeta(int meta) {
        return removeSign(this.getIntValueForMeta(meta));
    }

    @Override
    public int getMetaForPersistenceValue(String persistenceValue) {
        try {
            return this.getMetaForValue(addSign(Long.parseLong(persistenceValue)));
        } catch (NumberFormatException | InvalidBlockPropertyValueException e) {
            throw new InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, e);
        }
    }

    @Override
    public UnsignedIntBlockProperty copy() {
        return new UnsignedIntBlockProperty(this.getName(), this.isExportedToItem(), (int) this.getMaxValue(), (int) this.getMinValue(), this.getBitSize(), this.getPersistenceName());
    }

    @Override
    public UnsignedIntBlockProperty exportingToItems(boolean exportedToItem) {
        return new UnsignedIntBlockProperty(this.getName(), exportedToItem, (int) this.getMaxValue(), (int) this.getMinValue(), this.getBitSize(), this.getPersistenceName());
    }

    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }

    private static long removeSign(int value) {
        return (long) value & 0xFFFFFFFFL;
    }

    private static int addSign(long value) {
        return (int) (value & 0xFFFFFFFFL);
    }
}