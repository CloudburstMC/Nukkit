package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class BooleanBlockProperty extends BlockProperty<Boolean> {
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

    @Override
    public int getIntValue(int currentMeta, int bitOffset) {
        return getBooleanValue(currentMeta, bitOffset)? 1 : 0;
    }

    @Override
    public int getIntValueForMeta(int meta) {
        return meta & 1;
    }

    @Override
    public int getMetaForValue(@Nullable Boolean value) {
        return Boolean.TRUE.equals(value)? 1 : 0;
    }

    @Nonnull
    @Override
    public Boolean getValueForMeta(int meta) {
        return getBooleanValueForMeta(meta);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValueForMeta(int meta) {
        return (meta & 1) == 1;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    protected void validateMeta(int meta) {
        Preconditions.checkArgument(meta == 1 || meta == 0, "Must be 1 or 0");
    }

    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }

    @Override
    public String getPersistenceValueForMeta(int meta) {
        return meta == 1? "1" : "0";
    }
}
