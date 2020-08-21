package cn.nukkit.blockstate;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
public class IntMutableBlockState extends MutableBlockState {
    private int storage;
    
    public IntMutableBlockState(int blockId, BlockProperties properties, int state) {
        super(blockId, properties);
        this.storage = state;
    }
    
    public IntMutableBlockState(int blockId, BlockProperties properties) {
        this(blockId, properties, 0);
    }

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getLegacyDamage() {
        return storage & Block.DATA_MASK;
    }

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getBigDamage() {
        return storage;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public BigInteger getHugeDamage() {
        return BigInteger.valueOf(storage);
    }

    @Nonnull
    @Override
    public Integer getDataStorage() {
        return getBigDamage();
    }

    @Override
    public void setDataStorage(Number storage) {
        int state = storage.intValue();
        validate(state);
        this.storage = state;
    }

    @Override
    public void setDataStorageFromInt(int storage) {
        validate(storage);
        this.storage = storage;
    }

    @Override
    public void validate() {
        validate(storage);
    }
    
    private void validate(int state) {
        BlockProperties properties = this.properties;
        for (String name : properties.getNames()) {
            BlockProperty<?> property = properties.getBlockProperty(name);
            property.validateMeta(state, properties.getOffset(name));
        }
    }

    @Override
    public void setBooleanValue(String propertyName, boolean value) {
        storage = properties.setBooleanValue(storage, propertyName, value);
    }

    @Override
    public void setPropertyValue(String propertyName, @Nullable Object value) {
        storage = properties.setValue(storage, propertyName, value);
    }

    @Override
    public void setIntValue(String propertyName, int value) {
        storage = properties.setIntValue(storage, propertyName, value);
    }

    @Nonnull
    @Override
    public Object getPropertyValue(String propertyName) {
        return properties.getValue(storage, propertyName);
    }

    @Override
    public int getIntValue(String propertyName) {
        return properties.getIntValue(storage, propertyName);
    }

    @Override
    public boolean getBooleanValue(String propertyName) {
        return properties.getBooleanValue(storage, propertyName);
    }

    @Nonnull
    @Override
    public String getPersistenceValue(String propertyName) {
        return properties.getPersistenceValue(storage, propertyName);
    }

    @Nonnull
    @Override
    public BlockState getCurrentState() {
        return BlockState.of(blockId, storage);
    }

    @Override
    public int getExactIntStorage() {
        return storage;
    }

    @Nonnull
    @Override
    public IntMutableBlockState copy() {
        return new IntMutableBlockState(blockId, properties, storage);
    }
}
