package cn.nukkit.customblock.properties;

import cn.nukkit.customblock.properties.exception.BlockPropertyNotFoundException;
import cn.nukkit.utils.functional.ToIntTriFunctionTwoInts;
import cn.nukkit.utils.functional.ToLongTriFunctionOneIntOneLong;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.daporkchop.lib.common.function.plain.TriFunction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

public class BlockProperties {
    private final Map<String, RegisteredBlockProperty> byName;
    private final int bitSize;
    private final BlockProperties itemBlockProperties;

    public BlockProperties(BlockProperty<?>... properties) {
        this(null, properties);
    }

    public BlockProperties(BlockProperties itemBlockProperties, BlockProperty<?>... properties) {
        if (itemBlockProperties == null) {
            this.itemBlockProperties = this;
        } else {
            this.itemBlockProperties = itemBlockProperties;
        }

        Map<String, RegisteredBlockProperty> registry = new Object2ObjectLinkedOpenHashMap<>(properties.length);
        Map<String, RegisteredBlockProperty> byPersistenceName = new Object2ObjectLinkedOpenHashMap<>(properties.length);

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

    public BlockProperties getItemBlockProperties() {
        return this.itemBlockProperties;
    }

    @SuppressWarnings("unchecked")
    public int setValue(int currentMeta, String propertyName, Serializable value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public long setValue(long currentMeta, String propertyName, Serializable value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public int setBooleanValue(int currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.getProperty();
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        }

        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public long setBooleanValue(long currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.getProperty();
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        }

        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public BigInteger setBooleanValue(BigInteger currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.getProperty();
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        }

        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public int setIntValue(int currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.getProperty();
        if (IntBlockProperty.class == property.getClass()) {
            return ((IntBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        } else if (UnsignedIntBlockProperty.class == property.getClass()) {
            return ((UnsignedIntBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        }

        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public long setIntValue(long currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.getProperty();
        if (IntBlockProperty.class == property.getClass()) {
            return ((IntBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        } else if (UnsignedIntBlockProperty.class == property.getClass()) {
            return ((UnsignedIntBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        }

        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public BigInteger setIntValue(BigInteger currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.getProperty();
        if (IntBlockProperty.class == property.getClass()) {
            return ((IntBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        } else if (UnsignedIntBlockProperty.class == property.getClass()) {
            return ((UnsignedIntBlockProperty) property).setValue(currentMeta, registry.getOffset(), value);
        }

        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public int setPersistenceValue(int currentMeta, String propertyName, String persistenceValue) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty property = registry.getProperty();
        int meta = property.getMetaForPersistenceValue(persistenceValue);
        Serializable value = property.getValueForMeta(meta);
        return property.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public long setPersistenceValue(long currentMeta, String propertyName, String persistenceValue) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty property = registry.getProperty();
        int meta = property.getMetaForPersistenceValue(persistenceValue);
        Serializable value = property.getValueForMeta(meta);
        return property.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public BigInteger setPersistenceValue(BigInteger currentMeta, String propertyName, String persistenceValue) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty property = registry.getProperty();
        int meta = property.getMetaForPersistenceValue(persistenceValue);
        Serializable value = property.getValueForMeta(meta);
        return property.setValue(currentMeta, registry.getOffset(), value);
    }

    @SuppressWarnings("unchecked")
    public BigInteger setValue(BigInteger currentMeta, String propertyName, Serializable value) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        BlockProperty unchecked = registry.getProperty();
        return unchecked.setValue(currentMeta, registry.getOffset(), value);
    }

    public Serializable getValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getValue(currentMeta, registry.getOffset());
    }
    
    public Serializable getValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getValue(currentMeta, registry.getOffset());
    }
    
    public Serializable getValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getValue(currentMeta, registry.getOffset());
    }
    
    public <T> T getCheckedValue(int currentMeta, String propertyName, Class<T> clazz) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return clazz.cast(registry.getProperty().getValue(currentMeta, registry.getOffset()));
    }
    
    public <T> T getCheckedValue(long currentMeta, String propertyName, Class<T> clazz) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return clazz.cast(registry.getProperty().getValue(currentMeta, registry.getOffset()));
    }
    
    public <T> T getCheckedValue(BigInteger currentMeta, String propertyName, Class<T> clazz) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return clazz.cast(registry.getProperty().getValue(currentMeta, registry.getOffset()));
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return (T) registry.getProperty().getValue(currentMeta, registry.getOffset());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return (T) registry.getProperty().getValue(currentMeta, registry.getOffset());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return (T) registry.getProperty().getValue(currentMeta, registry.getOffset());
    }
    
    public int getIntValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getIntValue(currentMeta, registry.getOffset());
    }
    
    public int getIntValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getIntValue(currentMeta, registry.getOffset());
    }
    
    public int getIntValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getIntValue(currentMeta, registry.getOffset());
    }

    public Serializable getPersistenceValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getPersistenceValue(currentMeta, registry.getOffset());
    }

    public Serializable getPersistenceValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getPersistenceValue(currentMeta, registry.getOffset());
    }

    public Serializable getPersistenceValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        return registry.getProperty().getPersistenceValue(currentMeta, registry.getOffset());
    }

    public boolean getBooleanValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        if (registry.getProperty() instanceof BooleanBlockProperty) {
            return ((BooleanBlockProperty) registry.getProperty()).getBooleanValue(currentMeta, registry.getOffset());
        }

        return (Boolean) registry.getProperty().getValue(currentMeta, registry.getOffset());
    }

    public boolean getBooleanValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        if (registry.getProperty() instanceof BooleanBlockProperty) {
            return ((BooleanBlockProperty) registry.getProperty()).getBooleanValue(currentMeta, registry.getOffset());
        }

        return (Boolean) registry.getProperty().getValue(currentMeta, registry.getOffset());
    }

    public boolean getBooleanValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = requireRegisteredProperty(propertyName);
        if (registry.getProperty() instanceof BooleanBlockProperty) {
            return ((BooleanBlockProperty) registry.getProperty()).getBooleanValue(currentMeta, registry.getOffset());
        }

        return (Boolean) registry.getProperty().getValue(currentMeta, registry.getOffset());
    }

    public <R> R reduce(R identity, TriFunction<BlockProperty<?>, Integer, R, R> accumulator) {
        R result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.getProperty(), registry.getOffset(), result);
        }
        return result;
    }

    public int reduceInt(int identity, ToIntTriFunctionTwoInts<BlockProperty<?>> accumulator) {
        int result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.getProperty(), registry.getOffset(), result);
        }
        return result;
    }

    public long reduceLong(long identity, ToLongTriFunctionOneIntOneLong<BlockProperty<?>> accumulator) {
        long result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.getProperty(), registry.getOffset(), result);
        }
        return result;
    }

    public boolean isDefaultValue(String propertyName, Serializable value) {
        BlockProperty blockProperty = getBlockProperty(propertyName);
        return blockProperty.isDefaultValue(value);
    }
    
    public <T extends Serializable> boolean isDefaultValue(BlockProperty<T> property, T value) {
        return isDefaultValue(property.getName(), value);
    }
    
    public boolean isDefaultIntValue(String propertyName, int value) {
        BlockProperty blockProperty = getBlockProperty(propertyName);
        return blockProperty.isDefaultIntValue(value);
    }

    public <T extends Serializable> boolean isDefaultIntValue(BlockProperty<T> property, int value) {
        return isDefaultIntValue(property.getName(), value);
    }
    
    public boolean isDefaultBooleanValue(String propertyName, boolean value) {
        BlockProperty blockProperty = getBlockProperty(propertyName);
        return blockProperty.isDefaultBooleanValue(value);
    }

    public <T extends Serializable> boolean isDefaultBooleanValue(BlockProperty<T> property, boolean value) {
        return isDefaultBooleanValue(property.getName(), value);
    }

    public int getOffset(String propertyName) {
        return this.requireRegisteredProperty(propertyName).getOffset();
    }

    public boolean contains(String propertyName) {
        return this.byName.containsKey(propertyName);
    }

    public boolean contains(BlockProperty<?> property) {
        RegisteredBlockProperty registry = this.byName.get(property.getName());
        if (registry == null) {
            return false;
        }
        return registry.getProperty().getValueClass().equals(property.getValueClass());
    }

    @SuppressWarnings("java:S1452")
    public BlockProperty<?> getBlockProperty(String propertyName) {
        return this.requireRegisteredProperty(propertyName).getProperty();
    }

    public <T extends BlockProperty<?>> T getBlockProperty(String propertyName, Class<T> clazz) {
        return clazz.cast(this.requireRegisteredProperty(propertyName).getProperty());
    }

    public RegisteredBlockProperty requireRegisteredProperty(String propertyName) {
        RegisteredBlockProperty registry = this.byName.get(propertyName);
        if (registry == null) {
            throw new BlockPropertyNotFoundException(propertyName, this);
        }
        return registry;
    }

    public Set<String> getNames() {
        return this.byName.keySet();
    }

    public Collection<RegisteredBlockProperty> getAllProperties() {
        return this.byName.values();
    }

    public int getBitSize() {
        return this.bitSize;
    }

    public List<String> getItemPropertyNames() {
        List<String> itemProperties = new ArrayList<>(this.byName.size());
        for (RegisteredBlockProperty registry : this.byName.values()) {
            if (registry.getProperty().isExportedToItem()) {
                itemProperties.add(registry.getProperty().getName());
            } else {
                break;
            }
        }
        return itemProperties;
    }

    public void forEach(ObjIntConsumer<BlockProperty<?>> consumer) {
        for (RegisteredBlockProperty registry : this.byName.values()) {
            consumer.accept(registry.getProperty(), registry.getOffset());
        }
    }

    public void forEach(Consumer<BlockProperty<?>> consumer) {
        for (RegisteredBlockProperty registry : this.byName.values()) {
            consumer.accept(registry.getProperty());
        }
    }

    @Override
    public String toString() {
        return "BlockProperties{" +
                "bitSize=" + bitSize +
                ", properties=" + byName.values() +
                '}';
    }

}
