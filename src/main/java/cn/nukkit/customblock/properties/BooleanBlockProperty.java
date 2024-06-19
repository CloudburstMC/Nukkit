package cn.nukkit.customblock.properties;

import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyPersistenceValueException;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.math.BigInteger;

public class BooleanBlockProperty extends BlockProperty<Boolean> {
    private static final long serialVersionUID = 8249827149092664486L;

    public BooleanBlockProperty(String name, boolean exportedToItem, String persistenceName) {
        super(name, exportedToItem, persistenceName, 1);
    }

    public BooleanBlockProperty(String name, boolean exportedToItem) {
        super(name, exportedToItem, name, 1);
    }

    @Override
    public int setValue(int currentMeta, int bitOffset, Boolean newValue) {
        boolean value = newValue != null && newValue;
        return this.setValue(currentMeta, bitOffset, value);
    }

    @Override
    public long setValue(long currentBigMeta, int bitOffset, Boolean newValue) {
        boolean value = newValue != null && newValue;
        return this.setValue(currentBigMeta, bitOffset, value);
    }

    public int setValue(int currentMeta, int bitOffset, boolean newValue) {
        int mask = 1 << bitOffset;
        return newValue ? (currentMeta | mask) : (currentMeta & ~mask);
    }

    @Override
    public Boolean getValue(int currentMeta, int bitOffset) {
        return this.getBooleanValue(currentMeta, bitOffset);
    }

    @Override
    public Boolean getValue(long currentBigMeta, int bitOffset) {
        return this.getBooleanValue(currentBigMeta, bitOffset);
    }

    public boolean getBooleanValue(int currentMeta, int bitOffset) {
        int mask = 1 << bitOffset;
        return (currentMeta & mask) == mask;
    }

    public boolean getBooleanValue(long currentBigMeta, int bitOffset) {
        long mask = 1L << bitOffset;
        return (currentBigMeta & mask) == mask;
    }

    public boolean getBooleanValue(BigInteger currentHugeData, int bitOffset) {
        BigInteger mask = BigInteger.ONE.shiftLeft(bitOffset);
        return mask.equals(currentHugeData.and(mask));
    }

    @Override
    public int getIntValue(int currentMeta, int bitOffset) {
        return this.getBooleanValue(currentMeta, bitOffset)? 1 : 0;
    }

    @Override
    public int getIntValueForMeta(int meta) {
        if (meta == 1 || meta == 0) {
            return meta;
        }
        throw new InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected");
    }

    @Override
    public int getMetaForValue(Boolean value) {
        return Boolean.TRUE.equals(value)? 1 : 0;
    }

    @Override
    public Boolean getValueForMeta(int meta) {
        return this.getBooleanValueForMeta(meta);
    }

    public boolean getBooleanValueForMeta(int meta) {
        if (meta == 0) {
            return false;
        } else if (meta == 1) {
            return true;
        } else {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected");
        }
    }

    @Override
    public Serializable getPersistenceValueForMeta(int meta) {
        if (meta == 1) {
            return true;
        } else if (meta == 0) {
            return false;
        } else {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected");
        }
    }

    @Override
    public int getMetaForPersistenceValue(String persistenceValue) {
        if ("1".equals(persistenceValue)) {
            return 1;
        } else if ("0".equals(persistenceValue)) {
            return 0;
        } else {
            throw new InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, "Only 1 or 0 was expected");
        }
    }

    @Override
    public Boolean getDefaultValue() {
        return Boolean.FALSE;
    }

    @Override
    public boolean isDefaultValue(Boolean value) {
        return value == null || Boolean.FALSE.equals(value);
    }

    @Override
    protected void validateMetaDirectly(int meta) {
        Preconditions.checkArgument(meta == 1 || meta == 0, "Must be 1 or 0");
    }

    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }

    @Override
    public BooleanBlockProperty exportingToItems(boolean exportedToItem) {
        return new BooleanBlockProperty(this.getName(), exportedToItem, this.getPersistenceName());
    }

    @Override
    public BooleanBlockProperty copy() {
        return new BooleanBlockProperty(this.getName(), this.isExportedToItem(), this.getPersistenceName());
    }
}
