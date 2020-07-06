package cn.nukkit.blockstate;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockHyperMeta;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
public class BigIntegerBlockState extends BlockState {
    private BigInteger storage;
    
    public BigIntegerBlockState(int blockId, @Nonnull BlockProperties properties, BigInteger state) {
        super(blockId, properties);
        this.storage = state;
    }


    public BigIntegerBlockState(int blockId, @Nonnull BlockProperties properties) {
        this(blockId, properties, BigInteger.ZERO);
    }

    @Override
    public void setDataStorage(Number storage) {
        BigInteger state;
        if (storage instanceof BigInteger) {
            state = (BigInteger) storage;
        } else if (storage instanceof Long || storage instanceof Integer) {
            state = BigInteger.valueOf(storage.longValue());
        } else {
            state = new BigInteger(storage.toString());
        }
        validate(state);
        this.storage = state;
    }

    @Override
    public void setDataStorageFromInt(int storage) {
        BigInteger state = BigInteger.valueOf(storage);
        validate(state);
        this.storage = state;
    }

    @Override
    public void validate() {
        validate(storage);
    }
    
    private void validate(BigInteger state) {
        BlockProperties properties = this.properties;
        
        for (String name : properties.getNames()) {
            BlockProperty<?> property = properties.getBlockProperty(name);
            property.validateMeta(state, properties.getOffset(name));
        }
    }

    @Override
    public int getLegacyDamage() {
        return storage.and(BigInteger.valueOf(Block.DATA_MASK)).intValue();
    }

    @Override
    public int getHyperDamage() {
        return storage.and(BigInteger.valueOf(BlockHyperMeta.HYPER_DATA_MASK)).intValue();
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
    public BigIntegerBlockState copy() {
        return new BigIntegerBlockState(getBlockId(), properties, storage);
    }
}
