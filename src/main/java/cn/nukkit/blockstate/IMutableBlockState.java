package cn.nukkit.blockstate;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyException;
import cn.nukkit.blockstate.exception.InvalidBlockStateDataTypeException;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.math.NukkitMath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

import static cn.nukkit.blockstate.Loggers.logIMutableBlockState;

@ParametersAreNonnullByDefault
public interface IMutableBlockState extends IBlockState {
    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     * @throws InvalidBlockStateDataTypeException If the storage class type is not supported
     */
    void setDataStorage(Number storage);

    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     */
    void setDataStorageFromInt(int storage);

    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     * @throws InvalidBlockStateDataTypeException If the storage class type is not supported
     */
    default boolean setDataStorage(Number storage, boolean repair) {
        return setDataStorage(storage, repair, null);
    }

    /**
     * @return if the storage was repaired
     */
    default boolean setDataStorageFromInt(int storage, boolean repair) {
        return setDataStorageFromInt(storage, repair, null);
    }

    /**
     * @return if the storage was repaired
     * @throws InvalidBlockStateException If repair is false and the storage has an invalid property state
     * @throws InvalidBlockStateDataTypeException If the storage has an unsupported number type
     */
    default boolean setDataStorage(Number storage, boolean repair, @Nullable Consumer<BlockStateRepair> callback) {
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

                setDataStorage(repairStorage(getBlockId(), bigInteger, getProperties(), callback));
                return true;
            }
            throw e;
        }
    }

    /**
     * @return if the storage was repaired
     */
    default boolean setDataStorageFromInt(final int storage, boolean repair, @Nullable Consumer<BlockStateRepair> callback) {
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

    void setPropertyValue(String propertyName, @Nullable Serializable value);

    void setBooleanValue(String propertyName, boolean value);
    
    void setIntValue(String propertyName, int value);
    
    default void setBooleanValue(BlockProperty<Boolean> property, boolean value) {
        setBooleanValue(property.getName(), value);
    }
    
    default void setIntValue(BlockProperty<Integer> property, int value) {
        setIntValue(property.getName(), value);
    }

    default <T extends Serializable> void setPropertyValue(BlockProperty<T> property, @Nullable T value) {
        setPropertyValue(property.getName(), value);
    }
    
    default boolean toggleBooleanProperty(String propertyName) {
        boolean newValue = !getBooleanValue(propertyName);
        setBooleanValue(propertyName, newValue);
        return newValue;
    }
    
    default boolean toggleBooleanProperty(BlockProperty<Boolean> property) {
        return toggleBooleanProperty(property.getName());
    }
    
    @SuppressWarnings("unchecked")
    @Nonnull
    static BigInteger repairStorage(
            int blockId, @Nonnull final BigInteger storage, @Nonnull final BlockProperties properties, 
            @Nullable final Consumer<BlockStateRepair> callback) {
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
    
    static RuntimeException handleUnsupportedStorageType(int blockId, Number storage, RuntimeException e) {
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
