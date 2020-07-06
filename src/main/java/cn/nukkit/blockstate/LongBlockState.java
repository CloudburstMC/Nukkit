package cn.nukkit.blockstate;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockHyperMeta;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
public class LongBlockState extends BlockState {
    private long storage;
    
    public LongBlockState(int blockId, @Nonnull BlockProperties properties, long state) {
        super(blockId, properties);
        this.storage = state;
    }


    public LongBlockState(int blockId, @Nonnull BlockProperties properties) {
        this(blockId, properties, 0);
    }

    @Override
    public void setDataStorage(Number storage) {
        long state = storage.longValue();
        validate(state);
        this.storage = state;
    }

    @Override
    public void setDataStorageFromInt(int storage) {
        //noinspection UnnecessaryLocalVariable
        long state = storage;
        validate(state);
        this.storage = state;
    }

    @Override
    public void validate() {
        validate(storage);
    }
    
    private void validate(long state) {
        BlockProperties properties = this.properties;
        
        for (String name : properties.getNames()) {
            BlockProperty<?> property = properties.getBlockProperty(name);
            property.validateMeta(state, properties.getOffset(name));
        }
    }

    @Override
    public int getLegacyDamage() {
        return (int) (storage & Block.DATA_MASK);
    }

    @Override
    public int getHyperDamage() {
        return (int) (storage & BlockHyperMeta.HYPER_DATA_MASK);
    }

    @Override
    public Number getDataStorage() {
        return storage;
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
    public LongBlockState copy() {
        return new LongBlockState(getBlockId(), properties, storage);
    }
}
