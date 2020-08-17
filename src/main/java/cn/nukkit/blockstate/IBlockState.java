package cn.nukkit.blockstate;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.UnknownRuntimeIdException;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.HumanStringComparator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    @Nonnull
    Object getPropertyValue(String propertyName);
    
    @Nonnull
    default <V> V getPropertyValue(BlockProperty<V> property) {
        return getCheckedPropertyValue(property.getName(), property.getValueClass());
    }
    
    @Nonnull
    default <V> V getUncheckedPropertyValue(BlockProperty<V> property) {
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
    default String getPersistenceName() {
        return BlockStateRegistry.getPersistenceName(getBlockId());
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
    default String getLegacyStateId() {
        return getPersistenceName()+";nukkit-legacy="+getDataStorage();
    }

    @Nonnull
    BlockState getCurrentState();

    @Nonnull
    default Block getBlock() {
        Block block = Block.get(getBlockId());
        block.setDataStorage(getDataStorage());
        return block;
    }

    @Nonnull
    default Block getBlock(Level level, int x, int y, int z) {
        return getBlock(level, x, y, z, 0);
    }

    @Nonnull
    default Block getBlock(Level level, int x, int y, int z, int layer) {
        Block block = Block.get(getBlockId());
        block.setDataStorage(getDataStorage());
        block.level = level;
        block.x = x;
        block.y = y;
        block.z = z;
        block.layer = layer;
        return block;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(Position position) {
        return getBlock(position, 0);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default Block getBlock(Position position, int layer) {
        return getBlock(position.getLevel(), position.getFloorX(), position.getFloorY(), position.getFloorZ(), layer);
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

    default ItemBlock asItemBlock() {
        return asItemBlock(1);
    }

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
