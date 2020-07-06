package cn.nukkit.blockstate;

import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
public class IntMutableBlockState extends MutableBlockState {
    private int storage;
    
    public IntMutableBlockState(int blockId, @Nonnull BlockProperties properties, int state) {
        super(blockId, properties);
        this.storage = state;
    }
    
    public IntMutableBlockState(int blockId, @Nonnull BlockProperties properties) {
        this(blockId, properties, 0);
    }

    @Override
    public int getLegacyDamage() {
        return storage & Block.DATA_MASK;
    }

    @Override
    public int getHyperDamage() {
        return storage;
    }

    @Override
    public Integer getDataStorage() {
        return getHyperDamage();
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
    public void setPropertyValue(String propertyName, Object value) {
        storage = properties.setValue(storage, propertyName, value);
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

    @Override
    public String getPersistenceValue(String propertyName) {
        return properties.getPersistenceValue(storage, propertyName);
    }

    @Override
    public IntMutableBlockState copy() {
        return new IntMutableBlockState(blockId, properties, storage);
    }
}
