package cn.nukkit.blockstate;

import cn.nukkit.api.*;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.Validation;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static cn.nukkit.api.API.Definition.INTERNAL;
import static cn.nukkit.api.API.Usage.INCUBATING;
import static cn.nukkit.blockstate.IMutableBlockState.handleUnsupportedStorageType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
public class BigIntegerMutableBlockState extends MutableBlockState {
    private static final Set<Class<?>> LONG_COMPATIBLE_CLASSES = new HashSet<>(Arrays.asList(
            Long.class, Integer.class, Short.class, Byte.class));
    private BigInteger storage;
    
    public BigIntegerMutableBlockState(int blockId, BlockProperties properties, BigInteger state) {
        super(blockId, properties);
        this.storage = state;
    }


    public BigIntegerMutableBlockState(int blockId, BlockProperties properties) {
        this(blockId, properties, BigInteger.ZERO);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setDataStorage(@Nonnegative Number storage) {
        BigInteger state;
        if (storage instanceof BigInteger) {
            state = (BigInteger) storage;
        } else if (LONG_COMPATIBLE_CLASSES.contains(storage.getClass())) {
            state = BigInteger.valueOf(storage.longValue());
        } else {
            try {
                state = new BigDecimal(storage.toString()).toBigIntegerExact();
            } catch (NumberFormatException | ArithmeticException e) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e);
            }
        }
        validate(state);
        this.storage = state;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setDataStorageFromInt(@Nonnegative int storage) {
        BigInteger state = BigInteger.valueOf(storage);
        validate(state);
        this.storage = state;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    @API(definition = INTERNAL, usage = INCUBATING)
    void setDataStorageWithoutValidation(Number storage) {
        if (storage instanceof BigInteger) {
            this.storage = (BigInteger) storage;
        } else {
            this.storage = new BigInteger(storage.toString());
        }
    }

    @Override
    public void validate() {
        validate(storage);
    }
    
    private void validate(BigInteger state) {
        if (BigInteger.ZERO.equals(state)) {
            return;
        }
        Validation.checkPositive("state", state);
        BlockProperties properties = this.properties;
        int bitLength = NukkitMath.bitLength(state);
        if (bitLength > properties.getBitSize()) {
            throw new InvalidBlockStateException(
                    BlockState.of(getBlockId(), state),
                    "The state have more data bits than specified in the properties. Bits: " + bitLength + ", Max: " + properties.getBitSize()
            );
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

    @Nonnegative
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getLegacyDamage() {
        return storage.and(BigInteger.valueOf(Block.DATA_MASK)).intValue();
    }

    @Unsigned
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getBigDamage() {
        return storage.and(BigInteger.valueOf(BlockStateRegistry.BIG_META_MASK)).intValue();
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getSignedBigDamage() {
        return storage.and(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public BigInteger getHugeDamage() {
        return storage;
    }

    @Nonnegative
    @Nonnull
    @Override
    public Number getDataStorage() {
        return getHugeDamage();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultState() {
        return storage.equals(BigInteger.ONE);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setPropertyValue(String propertyName, @Nullable Serializable value) {
        storage = properties.setValue(storage, propertyName, value);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBooleanValue(String propertyName, boolean value) {
        storage = properties.setValue(storage, propertyName, value);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setIntValue(String propertyName, int value) {
        storage = properties.setValue(storage, propertyName, value);
    }

    @Nonnull
    @Override
    public Serializable getPropertyValue(String propertyName) {
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

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getExactIntStorage() {
        return storage.intValueExact();
    }

    @Nonnull
    @Override
    public BigIntegerMutableBlockState copy() {
        return new BigIntegerMutableBlockState(getBlockId(), properties, storage);
    }
}
