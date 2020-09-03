package cn.nukkit.blockstate;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.UnknownRuntimeIdException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

@ParametersAreNonnullByDefault
public interface IBlockState {
    int getBlockId();

    @Nonnull
    Number getDataStorage();

    @Nonnull
    BlockProperties getProperties();

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    int getLegacyDamage();

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    int getBigDamage();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    BigInteger getHugeDamage();

    /**
     * @throws InvalidBlockStateException
     */
    @Nonnull
    Object getPropertyValue(String propertyName);
    
    @Nonnull
    default <V extends Serializable> V getPropertyValue(BlockProperty<V> property) {
        return getCheckedPropertyValue(property.getName(), property.getValueClass());
    }
    
    @Nonnull
    default <V extends Serializable> V getUncheckedPropertyValue(BlockProperty<V> property) {
        return getUncheckedPropertyValue(property.getName());
    }

    int getIntValue(String propertyName);
    
    default int getIntValue(BlockProperty<?> property) {
        return getIntValue(property.getName());
    }

    boolean getBooleanValue(String propertyName);
    
    default boolean getBooleanValue(BlockProperty<?> property) {
        return getBooleanValue(property.getName());
    }

    @Nonnull
    String getPersistenceValue(String propertyName);
    
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
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default String getStateId() {
        BlockProperties properties = getProperties();
        Map<String, String> propertyMap = new TreeMap<>(HumanStringComparator.getInstance());
        properties.getNames().forEach(name-> propertyMap.put(properties.getBlockProperty(name).getPersistenceName(), getPersistenceValue(name)));

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

    @Nonnull
    BlockState getCurrentState();

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @Nonnull
    default Block getBlock() {
        Block block = Block.get(getBlockId());
        block.setDataStorage(getDataStorage());
        return block;
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @Nonnull
    default Block getBlock(@Nullable Level level, int x, int y, int z) {
        return getBlock(level, x, y, z, 0, false, null);
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @Nonnull
    default Block getBlock(@Nullable Level level, int x, int y, int z, int layer) {
        return getBlock(level, x, y, z, layer, false, null);
    }

    /**
     * @throws InvalidBlockStateException if repair is false and the state contains invalid property values
     */
    @Nonnull
    default Block getBlock(@Nullable Level level, int x, int y, int z, int layer, boolean repair) {
        return getBlock(level, x, y, z, layer, repair, null);
    }

    /**
     * @throws InvalidBlockStateException if repair is false and the state contains invalid property values
     */
    @Nonnull
    default Block getBlock(@Nullable Level level, int x, int y, int z, int layer, boolean repair, @Nullable BlockStateRepairCallback callback) {
        Block block = Block.get(getBlockId());
        block.level = level;
        block.x = x;
        block.y = y;
        block.z = z;
        block.layer = layer;
        block.setDataStorage(getDataStorage(), true, repair && callback == null? null : stateRepair -> {
            if (!repair) {
                throw new InvalidBlockStateException(getCurrentState(), "Invalid block state in layer "+layer+" at: "+new Position(x, y, z, level));
            }
            callback.onRepair(stateRepair);
        });
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

    @Nonnull
    default Block getBlockRepairing(@Nullable Level level, int x, int y, int z, int layer) {
        return getBlockRepairing(level, x, y, z, layer, null);
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlockRepairing(@Nullable Level level, int x, int y, int z, int layer, @Nullable BlockStateRepairCallback callback) {
        boolean callEvent1 = !BlockStateRepairEvent.getHandlers().isEmpty();
        List<BlockStateRepair> repairs = new ArrayList<>(0);
        PluginManager manager = callEvent1? Server.getInstance().getPluginManager() : null;
        Block block = getBlock(level, x, y, z, layer, true, !callEvent1? repairs::add : repair -> {
            manager.callEvent(new BlockStateRepairEvent(repair));
            repairs.add(repair);
            if (callback != null) {
                callback.onRepair(repair);
            }
        });
        
        if (!BlockStateRepairFinishEvent.getHandlers().isEmpty()) {
            BlockStateRepairFinishEvent event = new BlockStateRepairFinishEvent(repairs, block);
            Server.getInstance().getPluginManager().callEvent(event);
            block = event.getResult();
        }
        
        if (!repairs.isEmpty()) {
            Logger log = LogManager.getLogger(IBlockState.class);
            if (log.isDebugEnabled()) {
                log.debug("The block that at " + new Position(x, y, z, level) + " was repaired. Result: " + block + ", Repairs: " + repairs,
                        new Exception("Stacktrace")
                );
            }
        }
        return block;
    }

    default int getRuntimeId() {
        return BlockStateRegistry.getRuntimeId(getCurrentState());
    }

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

    @SuppressWarnings("rawtypes")
    @Nonnull
    default BlockProperty getProperty(String propertyName) {
        return getProperties().getBlockProperty(propertyName);
    }

    @Nonnull
    default <T extends BlockProperty<?>> T getCheckedProperty(String propertyName, Class<T> tClass) {
        return getProperties().getBlockProperty(propertyName, tClass);
    }

    @Nonnull
    default Set<String> getPropertyNames() {
        return getProperties().getNames();
    }

    @Nonnull
    default <T> T getCheckedPropertyValue(String propertyName, Class<T> tClass) {
        return tClass.cast(getPropertyValue(propertyName));
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    default <T> T getUncheckedPropertyValue(String propertyName) {
        return (T) getPropertyValue(propertyName);
    }

    default int getBitSize() {
        return getProperties().getBitSize();
    }

    int getExactIntStorage();

    @Nonnull
    default ItemBlock asItemBlock() {
        return asItemBlock(1);
    }

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
