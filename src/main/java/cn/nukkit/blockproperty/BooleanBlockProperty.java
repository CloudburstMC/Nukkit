package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class BooleanBlockProperty extends BlockProperty<Boolean> {
    private static final long serialVersionUID = 8249827149092664486L;
    
    private final boolean defaultValue;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BooleanBlockProperty(String name, boolean exportedToItem, boolean defaultValue, String persistenceName) {
        super(name, exportedToItem, 1, persistenceName);
        this.defaultValue = defaultValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BooleanBlockProperty(String name, boolean exportedToItem, boolean defaultValue) {
        super(name, exportedToItem, 1, name);
        this.defaultValue = defaultValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BooleanBlockProperty(String name, boolean exportedToItem) {
        this(name, exportedToItem, false);
    }

    @Override
    public int setValue(int currentMeta, int bitOffset, @Nullable Boolean newValue) {
        boolean value = newValue == null? defaultValue : newValue;
        return setValue(currentMeta, bitOffset, value);
    }

    @Override
    public long setValue(long currentBigMeta, int bitOffset, @Nullable Boolean newValue) {
        boolean value = newValue == null? defaultValue : newValue;
        return setValue(currentBigMeta, bitOffset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int setValue(int currentMeta, int bitOffset, boolean newValue) {
        int mask = 1 << bitOffset;
        return newValue? (currentMeta | mask) : (currentMeta & ~mask);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long setValue(long currentMeta, int bitOffset, boolean newValue) {
        long mask = 1L << bitOffset;
        return newValue? (currentMeta | mask) : (currentMeta & ~mask);
    }

    @Nonnull
    @Override
    public Boolean getValue(int currentMeta, int bitOffset) {
        return getBooleanValue(currentMeta, bitOffset);
    }

    @Nonnull
    @Override
    public Boolean getValue(long currentBigMeta, int bitOffset) {
        return getBooleanValue(currentBigMeta, bitOffset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(int currentMeta, int bitOffset) {
        int mask = 1 << bitOffset;
        return (currentMeta & mask) == mask;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(long currentBigMeta, int bitOffset) {
        long mask = 1L << bitOffset;
        return (currentBigMeta & mask) == mask;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(BigInteger currentHugeData, int bitOffset) {
        BigInteger mask = BigInteger.ONE.shiftLeft(bitOffset);
        return mask.equals(currentHugeData.and(mask));
    }

    @Override
    public int getIntValue(int currentMeta, int bitOffset) {
        return getBooleanValue(currentMeta, bitOffset)? 1 : 0;
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @Override
    public int getIntValueForMeta(int meta) {
        if (meta == 1 || meta == 0) {
            return meta;
        }
        throw new InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected");
    }

    @Override
    public int getMetaForValue(@Nullable Boolean value) {
        return Boolean.TRUE.equals(value)? 1 : 0;
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @Nonnull
    @Override
    public Boolean getValueForMeta(int meta) {
        return getBooleanValueForMeta(meta);
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValueForMeta(int meta) {
        if (meta == 0) {
            return false;
        } else if (meta == 1) {
            return true;
        } else {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected");
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    protected void validateMetaDirectly(int meta) {
        Preconditions.checkArgument(meta == 1 || meta == 0, "Must be 1 or 0");
    }

    @Nonnull
    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }

    @Override
    public String getPersistenceValueForMeta(int meta) {
        if (meta == 1) {
            return "1";
        } else if (meta == 0) {
            return "0";
        } else {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected");
        }
    }
}
