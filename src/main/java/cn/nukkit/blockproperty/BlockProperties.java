package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.exception.BlockPropertyNotFoundException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.blockstate.*;
import cn.nukkit.utils.functional.ToIntTriFunctionTwoInts;
import cn.nukkit.utils.functional.ToLongTriFunctionOneIntOneLong;
import cn.nukkit.utils.functional.TriFunction;
import com.google.common.base.Preconditions;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

@PowerNukkitOnly
@Since("1.4.0.0")
@ParametersAreNonnullByDefault
public final class BlockProperties {
    private final Map<String, RegisteredBlockProperty> byName;
    private final int bitSize;
    private final BlockProperties itemBlockProperties;

    /**
     * @throws IllegalArgumentException If there are validation failures
     */
    @PowerNukkitOnly
    @Since("1.4.0.0")
    public BlockProperties(BlockProperty<?>... properties) {
        this(null, properties);
    }

    /**
     * @throws IllegalArgumentException If there are validation failures
     */
    @PowerNukkitOnly
    @Since("1.4.0.0")
    public BlockProperties(@Nullable BlockProperties itemBlockProperties, BlockProperty<?>... properties) {
        if (itemBlockProperties == null) {
            this.itemBlockProperties = this;
        } else {
            this.itemBlockProperties = itemBlockProperties;
        }
        Map<String, RegisteredBlockProperty> registry = new LinkedHashMap<>(properties.length);
        Map<String, RegisteredBlockProperty> byPersistenceName = new LinkedHashMap<>(properties.length);
        int offset = 0;
        boolean allowItemExport = true;  
        for (BlockProperty<?> property : properties) {
            Preconditions.checkArgument(property != null, "The properties can not contains null values");
            if (property.isExportedToItem()) {
                Preconditions.checkArgument(allowItemExport, "Cannot export a property to item if the previous property does not export");
                Preconditions.checkArgument(offset <= 6); // Only 6 bits of data can be stored in item blocks, client side limitation.
            } else {
                allowItemExport = false;
            }

            RegisteredBlockProperty register = new RegisteredBlockProperty(property, offset);
            offset += property.getBitSize();


            Preconditions.checkArgument(registry.put(property.getName(), register) == null, "The property %s is duplicated by it's normal name", property.getName());
            Preconditions.checkArgument(byPersistenceName.put(property.getPersistenceName(), register) == null, "The property %s is duplicated by it's persistence name", property.getPersistenceName());
        }
        
        this.byName = Collections.unmodifiableMap(registry);
        bitSize = offset;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockProperties getItemBlockProperties() {
        return itemBlockProperties;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public MutableBlockState createMutableState(int blockId) {
        if (bitSize == 0) {
            return new ZeroMutableBlockState(blockId, this);
        } else if (bitSize < 8) {
            return new ByteMutableBlockState(blockId, this);
        } else if (bitSize < 32) {
            return new IntMutableBlockState(blockId, this);
        } else if (bitSize < 64) {
            return new LongMutableBlockState(blockId, this);
        } else {
            return new BigIntegerMutableBlockState(blockId, this);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean contains(String propertyName) {
        return byName.containsKey(propertyName);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean contains(BlockProperty<?> property) {
        RegisteredBlockProperty registry = byName.get(property.getName());
        if (registry == null) {
            return false;
        }
        return registry.getProperty().getValueClass().equals(property.getValueClass());
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("java:S1452")
    public BlockProperty<?> getBlockProperty(String propertyName) {
        return requireRegisteredProperty(propertyName).property;
    }
    /**
     * 
     * @throws NoSuchElementException If the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T extends BlockProperty<?>> T getBlockProperty(String propertyName, Class<T> tClass) {
        return tClass.cast(requireRegisteredProperty(propertyName).property);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getOffset(String propertyName) {
        return requireRegisteredProperty(propertyName).offset;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Set<String> getNames() {
        return byName.keySet();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Collection<RegisteredBlockProperty> getAllProperties() {
        return byName.values();
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public RegisteredBlockProperty requireRegisteredProperty(String propertyName) {
        RegisteredBlockProperty registry = byName.get(propertyName);
        if (registry == null) {
            throw new BlockPropertyNotFoundException(propertyName, this);
        }
        return registry;
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public int setValue(int currentMeta, String propertyName, @Nullable Serializable value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        @SuppressWarnings({"rawtypes", "java:S3740"}) 
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public long setValue(long currentMeta, String propertyName, @Nullable Serializable value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public int setBooleanValue(int currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }
        
        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public long setBooleanValue(long currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }

        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public BigInteger setBooleanValue(BigInteger currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }

        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public int setIntValue(int currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (IntBlockProperty.class == property.getClass()) {
            return ((IntBlockProperty) property).setValue(currentMeta, registry.offset, value);
        } else if (UnsignedIntBlockProperty.class == property.getClass()) {
            return ((UnsignedIntBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }

        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public long setIntValue(long currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (IntBlockProperty.class == property.getClass()) {
            return ((IntBlockProperty) property).setValue(currentMeta, registry.offset, value);
        } else if (UnsignedIntBlockProperty.class == property.getClass()) {
            return ((UnsignedIntBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }

        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public BigInteger setIntValue(BigInteger currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (IntBlockProperty.class == property.getClass()) {
            return ((IntBlockProperty) property).setValue(currentMeta, registry.offset, value);
        } else if (UnsignedIntBlockProperty.class == property.getClass()) {
            return ((UnsignedIntBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }

        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @SuppressWarnings("unchecked")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int setPersistenceValue(int currentMeta, String propertyName, String persistenceValue) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        @SuppressWarnings("rawtypes") 
        BlockProperty property = registry.property;
        int meta = property.getMetaForPersistenceValue(persistenceValue);
        Serializable value = property.getValueForMeta(meta);
        return property.setValue(currentMeta, registry.offset, value);
    }

    @SuppressWarnings("unchecked")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long setPersistenceValue(long currentMeta, String propertyName, String persistenceValue) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        @SuppressWarnings("rawtypes")
        BlockProperty property = registry.property;
        int meta = property.getMetaForPersistenceValue(persistenceValue);
        Serializable value = property.getValueForMeta(meta);
        return property.setValue(currentMeta, registry.offset, value);
    }

    @SuppressWarnings("unchecked")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BigInteger setPersistenceValue(BigInteger currentMeta, String propertyName, String persistenceValue) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        @SuppressWarnings("rawtypes")
        BlockProperty property = registry.property;
        int meta = property.getMetaForPersistenceValue(persistenceValue);
        Serializable value = property.getValueForMeta(meta);
        return property.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    @Nonnull
    public BigInteger setValue(BigInteger currentMeta, String propertyName, @Nullable Serializable value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Serializable getValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Serializable getValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Serializable getValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property value is not assignable to the given class
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(int currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset));
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property value is not assignable to the given class
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(long currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset));
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property value is not assignable to the given class
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(BigInteger currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset));
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return (T) registry.property.getValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return (T) registry.property.getValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return (T) registry.property.getValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getIntValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getIntValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getIntValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    public String getPersistenceValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getPersistenceValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    public String getPersistenceValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getPersistenceValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    public String getPersistenceValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.property.getPersistenceValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        if (registry.property instanceof BooleanBlockProperty) {
            return ((BooleanBlockProperty) registry.property).getBooleanValue(currentMeta, registry.offset);
        }

        return (Boolean) registry.property.getValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        if (registry.property instanceof BooleanBlockProperty) {
            return ((BooleanBlockProperty) registry.property).getBooleanValue(currentMeta, registry.offset);
        }

        return (Boolean) registry.property.getValue(currentMeta, registry.offset);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        if (registry.property instanceof BooleanBlockProperty) {
            return ((BooleanBlockProperty) registry.property).getBooleanValue(currentMeta, registry.offset);
        }

        return (Boolean) registry.property.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBitSize() {
        return bitSize;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void forEach(ObjIntConsumer<BlockProperty<?>> consumer) {
        for (RegisteredBlockProperty registry : byName.values()) {
            consumer.accept(registry.property, registry.offset);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void forEach(Consumer<BlockProperty<?>> consumer) {
        for (RegisteredBlockProperty registry : byName.values()) {
            consumer.accept(registry.property);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public <R> R reduce(R identity, TriFunction<BlockProperty<?>, Integer, R, R> accumulator) {
        R result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result);
        }
        return result;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int reduceInt(int identity, ToIntTriFunctionTwoInts<BlockProperty<?>> accumulator) {
        int result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result);
        }
        return result;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long reduceLong(long identity, ToLongTriFunctionOneIntOneLong<BlockProperty<?>> accumulator) {
        long result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result);
        }
        return result;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public List<String> getItemPropertyNames() {
        List<String> itemProperties = new ArrayList<>(byName.size());
        for (RegisteredBlockProperty registry : byName.values()) {
            if (registry.property.isExportedToItem()) {
                itemProperties.add(registry.property.getName());
            } else {
                break;
            }
        }
        return itemProperties;
    }

    @Override
    public String toString() {
        return "BlockProperties{" +
                "bitSize=" + bitSize +
                ", properties=" + byName.values() +
                '}';
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings({"rawtypes", "java:S3740", "unchecked"})
    public boolean isDefaultValue(String propertyName, @Nullable Serializable value) {
        BlockProperty blockProperty = getBlockProperty(propertyName);
        return blockProperty.isDefaultValue(value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public <T extends Serializable> boolean isDefaultValue(BlockProperty<T> property, @Nullable T value) {
        return isDefaultValue(property.getName(), value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings({"rawtypes", "java:S3740"})
    public boolean isDefaultIntValue(String propertyName, int value) {
        BlockProperty blockProperty = getBlockProperty(propertyName);
        return blockProperty.isDefaultIntValue(value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public <T extends Serializable> boolean isDefaultIntValue(BlockProperty<T> property, int value) {
        return isDefaultIntValue(property.getName(), value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings({"rawtypes", "java:S3740"})
    public boolean isDefaultBooleanValue(String propertyName, boolean value) {
        BlockProperty blockProperty = getBlockProperty(propertyName);
        return blockProperty.isDefaultBooleanValue(value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public <T extends Serializable> boolean isDefaultBooleanValue(BlockProperty<T> property, boolean value) {
        return isDefaultBooleanValue(property.getName(), value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Value
    public static class RegisteredBlockProperty {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        BlockProperty<?> property;

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        int offset;

        /**
         * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public void validateMeta(int meta) {
            property.validateMeta(meta, offset);
        }

        /**
         * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public void validateMeta(long meta) {
            property.validateMeta(meta, offset);
        }

        /**
         * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public void validateMeta(BigInteger meta) {
            property.validateMeta(meta, offset);
        }
        
        @Override
        public String toString() {
            return offset+"-"+(offset+property.getBitSize())+":"+property.getName();
        }
    }
}
