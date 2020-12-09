package cn.nukkit.blockstate;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.api.Unsigned;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.UnknownRuntimeIdException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.event.blockstate.BlockStateRepairEvent;
import cn.nukkit.event.blockstate.BlockStateRepairFinishEvent;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.HumanStringComparator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;

import static cn.nukkit.blockstate.Loggers.logIBlockState;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public interface IBlockState {
    @Nonnegative
    int getBlockId();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Nonnegative
    Number getDataStorage();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    boolean isDefaultState();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    BlockProperties getProperties();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Nonnegative
    int getLegacyDamage();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Unsigned
    int getBigDamage();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Nonnegative
    default int getSignedBigDamage() {
        return getBigDamage();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Nonnegative
    BigInteger getHugeDamage();

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    Serializable getPropertyValue(String propertyName);

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     * @throws ClassCastException If the actual property value don't match the type of the given property 
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default <V extends Serializable> V getPropertyValue(BlockProperty<V> property) {
        return getCheckedPropertyValue(property.getName(), property.getValueClass());
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default <V extends Serializable> V getUncheckedPropertyValue(BlockProperty<V> property) {
        return getUncheckedPropertyValue(property.getName());
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int getIntValue(String propertyName);

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default int getIntValue(BlockProperty<?> property) {
        return getIntValue(property.getName());
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    boolean getBooleanValue(String propertyName);

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean getBooleanValue(BlockProperty<?> property) {
        return getBooleanValue(property.getName());
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    String getPersistenceValue(String propertyName);

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default String getPersistenceValue(BlockProperty<?> property) {
        return getPersistenceValue(property.getName());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default String getPersistenceName() {
        return BlockStateRegistry.getPersistenceName(getBlockId());
    }

    /**
     * Gets a unique persistence identification for this state based on the block properties.
     * <p>If the state holds an invalid meta, the result of {@link #getLegacyStateId()} is returned.</p>
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default String getStateId() {
        BlockProperties properties = getProperties();
        Map<String, String> propertyMap = new TreeMap<>(HumanStringComparator.getInstance());
        try {
            properties.getNames().forEach(name -> propertyMap.put(properties.getBlockProperty(name).getPersistenceName(), getPersistenceValue(name)));
        } catch (InvalidBlockPropertyException e) {
            logIBlockState.debug("Attempted to get the stateId of an invalid state {}:{}\nProperties: {}", getBlockId(), getDataStorage(), properties, e);
            return getLegacyStateId();
        }

        StringBuilder stateId = new StringBuilder(getPersistenceName());
        propertyMap.forEach((name, value) -> stateId.append(';').append(name).append('=').append(value));
        return stateId.toString();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default String getLegacyStateId() {
        return getPersistenceName()+";nukkit-legacy="+getDataStorage();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    BlockState getCurrentState();

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock() {
        Block block = Block.get(getBlockId());
        block.setState(this);
        return block;
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(@Nullable Level level, int x, int y, int z) {
        return getBlock(level, x, y, z, 0, false, null);
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(@Nullable Level level, int x, int y, int z, int layer) {
        return getBlock(level, x, y, z, layer, false, null);
    }

    /**
     * @throws InvalidBlockStateException if repair is false and the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(@Nullable Level level, int x, int y, int z, int layer, boolean repair) {
        return getBlock(level, x, y, z, layer, repair, null);
    }

    /**
     * @throws InvalidBlockStateException if repair is false and the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(@Nullable Level level, int x, int y, int z, int layer, boolean repair, @Nullable Consumer<BlockStateRepair> callback) {
        Block block = Block.get(getBlockId());
        block.level = level;
        block.x = x;
        block.y = y;
        block.z = z;
        block.layer = layer;
        BlockState currentState = getCurrentState();
        try {
            if (currentState.isCachedValidationValid()) {
                block.setState(currentState);
                return block;
            }
        } catch (Exception e) {
            logIBlockState.error("Unexpected error while trying to set the cached valid state to the block. State: {}, Block: {}", currentState, block, e);
        }
        
        try {
            block.setDataStorage(currentState.getDataStorage(), repair, callback);
        } catch (InvalidBlockStateException e) {
            throw new InvalidBlockStateException(getCurrentState(), "Invalid block state in layer "+layer+" at: "+new Position(x, y, z, level), e);
        }
        return block;
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(Position position) {
        return getBlock(position, 0);
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(Block position) {
        return getBlock(position, position.layer);
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(Position position, int layer) {
        return getBlock(position.getLevel(), position.getFloorX(), position.getFloorY(), position.getFloorZ(), layer);
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(Block pos) {
        return getBlockRepairing(pos, pos.layer);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(Position position, int layer) {
        return getBlockRepairing(position.level, position, layer);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(@Nullable Level level, BlockVector3 pos, int layer) {
        return getBlockRepairing(level, pos.x, pos.y, pos.z, layer);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(@Nullable Level level, Vector3 pos) {
        return getBlockRepairing(level, pos, 0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(@Nullable Level level, Vector3 pos, int layer) {
        return getBlockRepairing(level, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(@Nullable Level level, int x, int y, int z) {
        return getBlockRepairing(level, x, y, z, 0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(@Nullable Level level, int x, int y, int z, int layer) {
        return getBlockRepairing(level, x, y, z, layer, null);
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(@Nullable Level level, int x, int y, int z, int layer, @Nullable Consumer<BlockStateRepair> callback) {
        List<BlockStateRepair> repairs = new ArrayList<>(0);
        
        Consumer<BlockStateRepair> callbackChain = repairs::add;

        if (!BlockStateRepairEvent.getHandlers().isEmpty()) {
            PluginManager manager = Server.getInstance().getPluginManager();
            callbackChain = callbackChain.andThen(repair -> manager.callEvent(new BlockStateRepairEvent(repair)));
        }
        
        if (callback != null) {
            callbackChain = callbackChain.andThen(callback);
        }
        
        Block block = getBlock(level, x, y, z, layer, true, callbackChain);
        
        if (!BlockStateRepairFinishEvent.getHandlers().isEmpty()) {
            BlockStateRepairFinishEvent event = new BlockStateRepairFinishEvent(repairs, block);
            Server.getInstance().getPluginManager().callEvent(event);
            block = event.getResult();
        }

        if (!repairs.isEmpty() && logIBlockState.isDebugEnabled()) {
            logIBlockState.debug("The block that at Level:{}, X:{}, Y:{}, Z:{}, L:{} was repaired. Result: {}, Repairs: {}",
                    level, x, y, z, layer, block, repairs,
                    new Exception("Stacktrace")
            );
        }
        
        return block;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default int getRuntimeId() {
        return BlockStateRegistry.getRuntimeId(getCurrentState());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself")
    default int getFullId() {
        return (getBlockId() << Block.DATA_BITS) | (getLegacyDamage() & Block.DATA_MASK);
    }

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself")
    default long getBigId() {
        return ((long)getBlockId() << 32) | (getBigDamage() & BlockStateRegistry.BIG_META_MASK);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("rawtypes")
    @Nonnull
    default BlockProperty getProperty(String propertyName) {
        return getProperties().getBlockProperty(propertyName);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default <T extends BlockProperty<?>> T getCheckedProperty(String propertyName, Class<T> tClass) {
        return getProperties().getBlockProperty(propertyName, tClass);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Set<String> getPropertyNames() {
        return getProperties().getNames();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default <T> T getCheckedPropertyValue(String propertyName, Class<T> tClass) {
        return tClass.cast(getPropertyValue(propertyName));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    default <T> T getUncheckedPropertyValue(String propertyName) {
        return (T) getPropertyValue(propertyName);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default int getBitSize() {
        return getProperties().getBitSize();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnegative
    int getExactIntStorage();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default ItemBlock asItemBlock() {
        return asItemBlock(1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default ItemBlock asItemBlock(int count) {
        BlockState currentState = getCurrentState();
        BlockState itemState = currentState.forItem();
        int runtimeId = itemState.getRuntimeId();
        if (runtimeId == BlockStateRegistry.getUpdateBlockRegistration() && !"minecraft:info_update".equals(itemState.getPersistenceName())) {
            throw new UnknownRuntimeIdException("The current block state can't be represented as an item. State: "+currentState+", Item: "+itemState);
        }
        Block block = itemState.getBlock();
        return new ItemBlock(block, itemState.getExactIntStorage(), count);
    }
}
