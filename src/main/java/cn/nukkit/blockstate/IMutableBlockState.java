package cn.nukkit.blockstate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyException;
import cn.nukkit.blockstate.exception.InvalidBlockStateDataTypeException;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.Validation;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

import static cn.nukkit.blockstate.Loggers.logIMutableBlockState;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public interface IMutableBlockState extends IBlockState {
    /**
     * Replace all matching states of this block state with the same states of the given block state.
     * <p>States that doesn't exists in the other state are ignored.
     * <p>Only properties that matches each other will be copied, for example, if this state have an age property
     * going from 0 to 7 and the other have an age from 0 to 15, the age property won't change.
     * @throws UnsupportedOperationException If the state is from a different block id and property copying isn't supported by the implementation
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     * @param state The states that will have the properties copied.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default void setState(IBlockState state) throws InvalidBlockStateException {
        if (state.getBlockId() == getBlockId()) {
            setDataStorage(state.getDataStorage());
        } else {
            //TODO Implement property value copying
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     * @throws InvalidBlockStateDataTypeException If the storage class type is not supported
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void setDataStorage(@Nonnegative Number storage);

    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void setDataStorageFromInt(@Nonnegative int storage);

    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     * @throws InvalidBlockStateDataTypeException If the storage class type is not supported
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean setDataStorage(@Nonnegative Number storage, boolean repair) {
        return setDataStorage(storage, repair, null);
    }

    /**
     * @return if the storage was repaired
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean setDataStorageFromInt(@Nonnegative int storage, boolean repair) {
        return setDataStorageFromInt(storage, repair, null);
    }

    /**
     * @return if the storage was repaired
     * @throws InvalidBlockStateException If repair is false and the storage has an invalid property state
     * @throws InvalidBlockStateDataTypeException If the storage has an unsupported number type
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean setDataStorage(@Nonnegative Number storage, boolean repair, @Nullable Consumer<BlockStateRepair> callback) {
        try {
            setDataStorage(storage);
            return false;
        } catch (InvalidBlockStateException e) {
            if (repair) {
                BigInteger bigInteger;
                try {
                    bigInteger = new BigDecimal(storage.toString()).toBigIntegerExact();
                } catch (NumberFormatException | ArithmeticException e2) {
                    InvalidBlockStateDataTypeException ex = new InvalidBlockStateDataTypeException(storage, e2);
                    ex.addSuppressed(e);
                    throw ex;
                }
                
                try {
                    setDataStorage(repairStorage(getBlockId(), bigInteger, getProperties(), callback));
                } catch (InvalidBlockPropertyException | InvalidBlockStateException e2) {
                    InvalidBlockStateException ex = new InvalidBlockStateException(e.getState(), "The state is invalid and could not be repaired", e);
                    ex.addSuppressed(e2);
                    throw ex;
                }
                return true;
            }
            throw e;
        }
    }

    /**
     * @return if the storage was repaired
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean setDataStorageFromInt(@Nonnegative final int storage, boolean repair, @Nullable Consumer<BlockStateRepair> callback) {
        try {
            setDataStorageFromInt(storage);
            return false;
        } catch (IllegalStateException | InvalidBlockPropertyException e) {
            if (repair) {
                setDataStorage(repairStorage(getBlockId(), BigInteger.valueOf(storage), getProperties(), callback));
                return true;
            }
            throw e;
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default void setDataStorageFromItemBlockMeta(int itemBlockMeta) {
        BlockProperties allProperties = getProperties();
        BlockProperties itemBlockProperties = allProperties.getItemBlockProperties();
        if (allProperties.equals(itemBlockProperties)) {
            setDataStorageFromInt(itemBlockMeta);
            return;
        }
        
        MutableBlockState item = itemBlockProperties.createMutableState(getBlockId());
        item.setDataStorageFromInt(itemBlockMeta);

        MutableBlockState converted = allProperties.createMutableState(getBlockId());
        itemBlockProperties.getItemPropertyNames().forEach(property ->
                converted.setPropertyValue(property, item.getPropertyValue(property)));
        setDataStorage(converted.getDataStorage());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void setPropertyValue(String propertyName, @Nullable Serializable value);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void setBooleanValue(String propertyName, boolean value);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    void setIntValue(String propertyName, int value);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default void setBooleanValue(BlockProperty<Boolean> property, boolean value) {
        setBooleanValue(property.getName(), value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default void setIntValue(BlockProperty<Integer> property, int value) {
        setIntValue(property.getName(), value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default <T extends Serializable> void setPropertyValue(BlockProperty<T> property, @Nullable T value) {
        setPropertyValue(property.getName(), value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean toggleBooleanProperty(String propertyName) {
        boolean newValue = !getBooleanValue(propertyName);
        setBooleanValue(propertyName, newValue);
        return newValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean toggleBooleanProperty(BlockProperty<Boolean> property) {
        return toggleBooleanProperty(property.getName());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    @Nonnull
    static BigInteger repairStorage(
            @Nonnegative int blockId, @Nonnull final BigInteger storage, @Nonnull final BlockProperties properties, 
            @Nullable final Consumer<BlockStateRepair> callback) {
        Validation.checkPositive("blockId", blockId);
        
        int checkedBits = 0;
        int repairs = 0;
        BigInteger current = storage;
        for (BlockProperties.RegisteredBlockProperty reg : properties.getAllProperties()) {
            checkedBits += reg.getProperty().getBitSize();
            try {
                reg.validateMeta(current);
            } catch (InvalidBlockPropertyException e) {
                BlockProperty<?> property = reg.getProperty();
                int offset = reg.getOffset();
                BigInteger next = property.setValue(current, offset, null);
                if (callback != null) {
                    Serializable fixed = property.getValue(next, offset);
                    BlockStateRepair stateRepair = new BlockStateRepair(
                            blockId, properties,
                            storage, current, next, repairs++, property, offset,
                            property.getMetaFromBigInt(current, offset),
                            fixed, fixed, e
                    );
                    callback.accept(stateRepair);
                    Serializable proposed = stateRepair.getProposedPropertyValue();
                    if (!fixed.equals(proposed)) {
                        try {
                            next = ((BlockProperty<Serializable>) property).setValue(current, offset, proposed);
                        } catch (InvalidBlockPropertyException proposedFailed) {
                            logIMutableBlockState.warn("Could not apply the proposed repair, using the default proposal. "+stateRepair, proposedFailed);
                        }
                    }
                }
                current = next;
            }
        }

        if (NukkitMath.bitLength(current) > checkedBits) {
            BigInteger validMask = BigInteger.ONE.shiftLeft(checkedBits).subtract(BigInteger.ONE);
            BigInteger next = current.and(validMask);
            if (callback != null) {
                BlockStateRepair stateRepair = new BlockStateRepair(
                        blockId, properties,
                        storage, current, next, repairs, null,
                        checkedBits, current.shiftRight(checkedBits).intValue(), 0, 0,
                        null);
                callback.accept(stateRepair);
                if (!Integer.valueOf(0).equals(stateRepair.getProposedPropertyValue())) {
                    logIMutableBlockState.warn("Could not apply the proposed repair, using the default proposal. "+stateRepair, 
                            new IllegalStateException("Attempted to propose a value outside the properties boundary"));
                }
            }
            current = next;
        }
        
        return current;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    static RuntimeException handleUnsupportedStorageType(@Nonnegative int blockId, @Nonnegative Number storage, RuntimeException e) {
        InvalidBlockStateException ex;
        try {
            ex = new InvalidBlockStateException(BlockState.of(blockId, storage), e);
        } catch (InvalidBlockStateDataTypeException e2) {
            e2.addSuppressed(e);
            return e2;
        }
        return ex;
    }
}
