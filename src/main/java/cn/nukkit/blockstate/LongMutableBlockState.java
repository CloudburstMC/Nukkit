package cn.nukkit.blockstate;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyException;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.math.NukkitMath;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import static cn.nukkit.blockstate.IMutableBlockState.handleUnsupportedStorageType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
public class LongMutableBlockState extends MutableBlockState {
    private long storage;
    
    public LongMutableBlockState(int blockId,BlockProperties properties, long state) {
        super(blockId, properties);
        this.storage = state;
    }


    public LongMutableBlockState(int blockId, BlockProperties properties) {
        this(blockId, properties, 0);
    }

    @Override
    public void setDataStorage(Number storage) {
        Class<? extends Number> c = storage.getClass();
        long state;
        if (c == Long.class || c == Integer.class || c == Short.class || c == Byte.class) {
            state = storage.longValue();
        } else {
            try {
                state = new BigDecimal(storage.toString()).longValueExact();
            } catch (ArithmeticException | NumberFormatException e) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e);
            }
        }
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
        if (state != 0) {
            int bitLength = NukkitMath.bitLength(state);
            if (bitLength > properties.getBitSize()) {
                throw new InvalidBlockStateException(
                        BlockState.of(getBlockId(), state),
                        "The state have more data bits than specified in the properties. Bits: " + bitLength + ", Max: " + properties.getBitSize()
                );
            }
        }
        
        try {
            for (String name : properties.getNames()) {
                BlockProperty<?> property = properties.getBlockProperty(name);
                property.validateMeta(state, properties.getOffset(name));
            }
        } catch (InvalidBlockPropertyException e) {
            throw new InvalidBlockStateException(BlockState.of(getBlockId(), state), e);
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getLegacyDamage() {
        return (int) (storage & Block.DATA_MASK);
    }

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getBigDamage() {
        return (int) (storage & BlockStateRegistry.BIG_META_MASK);
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
    public Number getDataStorage() {
        return storage;
    }

    @Override
    public void setPropertyValue(String propertyName, @Nullable Serializable value) {
        storage = properties.setValue(storage, propertyName, value);
    }

    @Override
    public void setBooleanValue(String propertyName, boolean value) {
        storage = properties.setBooleanValue(storage, propertyName, value);
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
        int bits = getBitSize();
        if (bits > 32) {
            throw new ArithmeticException(storage+" can't be stored in an 32 bits integer. It has "+bits+" bits");
        }
        return (int) storage;
    }

    @Nonnull
    @Override
    public LongMutableBlockState copy() {
        return new LongMutableBlockState(getBlockId(), properties, storage);
    }
}
