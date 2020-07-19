package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BigIntegerMutableBlockState;
import cn.nukkit.blockstate.IntMutableBlockState;
import cn.nukkit.blockstate.LongMutableBlockState;
import cn.nukkit.blockstate.MutableBlockState;
import cn.nukkit.utils.functional.ToIntTriFunctionTwoInts;
import cn.nukkit.utils.functional.ToLongTriFunctionOneIntOneLong;
import cn.nukkit.utils.functional.TriFunction;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
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

    @PowerNukkitOnly
    @Since("1.4.0.0")
    public BlockProperties(BlockProperty<?>... properties) {
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
    
    public MutableBlockState createMutableState(int blockId) {
        if (bitSize <= 32) {
            return new IntMutableBlockState(blockId, this);
        } else if (bitSize <= 64) {
            return new LongMutableBlockState(blockId, this);
        } else {
            return new BigIntegerMutableBlockState(blockId, this);
        }
    }
    
    public MutableBlockState createMutableState(int blockId, Number storage) {
        if (bitSize <= 32) {
            if (storage instanceof Integer) {
                return new IntMutableBlockState(blockId, this, storage.intValue());
            } else {
                throw new IllegalArgumentException("Incompatible storage type "+storage.getClass()+", expected Integer");
            }
        } else if (bitSize <= 64) {
            if (storage instanceof Long || storage instanceof Integer) {
                return new LongMutableBlockState(blockId, this, storage.longValue());
            } else {
                throw new IllegalArgumentException("Incompatible storage type "+storage.getClass()+", expected Long or Integer");
            }
        }
        
        if (storage instanceof BigInteger) {
            return new BigIntegerMutableBlockState(blockId, this, (BigInteger) storage);
        } else if (storage instanceof Long || storage instanceof Integer) {
            return new BigIntegerMutableBlockState(blockId, this, new BigInteger(storage.toString()));
        } else {
            throw new IllegalArgumentException("Incompatible storage type "+storage.getClass()+", expected BigInteger, Long or Integer");
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean contains(String propertyName) {
        return getRegisteredProperty(propertyName) != null;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("java:S1452")
    public BlockProperty<?> getBlockProperty(String propertyName) {
        return getRegisteredProperty(propertyName).property;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public <T extends BlockProperty<?>> T getBlockProperty(String propertyName, Class<T> tClass) {
        return tClass.cast(getRegisteredProperty(propertyName).property);
    }
    
    public int getOffset(String propertyName) {
        return getRegisteredProperty(propertyName).offset;
    }
    
    public Set<String> getNames() {
        return byName.keySet();
    }
    
    private RegisteredBlockProperty getRegisteredProperty(String propertyName) {
        RegisteredBlockProperty registry = byName.get(propertyName);
        return Preconditions.checkNotNull(registry, "The property %s was not found", propertyName);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public int setValue(int currentMeta, String propertyName, @Nullable Object value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        @SuppressWarnings({"rawtypes", "java:S3740"}) 
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public long setValue(long currentMeta, String propertyName, @Nullable Object value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public int setBooleanValue(int currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }
        
        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public long setBooleanValue(long currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }

        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public BigInteger setBooleanValue(BigInteger currentMeta, String propertyName, boolean value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        BlockProperty<?> property = registry.property;
        if (BooleanBlockProperty.class == property.getClass()) {
            return ((BooleanBlockProperty) property).setValue(currentMeta, registry.offset, value);
        }

        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public int setIntValue(int currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public long setIntValue(long currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public BigInteger setIntValue(BigInteger currentMeta, String propertyName, int value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    @Nonnull
    public BigInteger setValue(BigInteger currentMeta, String propertyName, @Nullable Object value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        @SuppressWarnings({"rawtypes", "java:S3740"})
        BlockProperty unchecked = registry.property;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Object getValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Object getValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Object getValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(int currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(long currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(BigInteger currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return (T) registry.property.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return (T) registry.property.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return (T) registry.property.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getIntValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getIntValue(currentMeta, registry.offset);
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getIntValue(currentMeta, registry.offset);
    }
    
    public String getPersistenceValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getPersistenceValue(currentMeta, registry.offset);
    }

    public String getPersistenceValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getPersistenceValue(currentMeta, registry.offset);
    }

    public String getPersistenceValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.property.getPersistenceValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(int currentMeta, String propertyName) {
        return getIntValue(currentMeta, propertyName) == 1;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(long currentMeta, String propertyName) {
        return getIntValue(currentMeta, propertyName) == 1;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getBooleanValue(BigInteger currentMeta, String propertyName) {
        return getIntValue(currentMeta, propertyName) == 1;
    }

    public int getBitSize() {
        return bitSize;
    }

    public void forEach(ObjIntConsumer<BlockProperty<?>> consumer) {
        for (RegisteredBlockProperty registry : byName.values()) {
            consumer.accept(registry.property, registry.offset);
        }
    }
    
    public void forEach(Consumer<BlockProperty<?>> consumer) {
        for (RegisteredBlockProperty registry : byName.values()) {
            consumer.accept(registry.property);
        }
    }

    public <R> R reduce(R identity, TriFunction<BlockProperty<?>, Integer, R, R> accumulator) {
        R result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result);
        }
        return result;
    }

    public int reduceInt(int identity, ToIntTriFunctionTwoInts<BlockProperty<?>> accumulator) {
        int result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result);
        }
        return result;
    }

    public long reduceLong(long identity, ToLongTriFunctionOneIntOneLong<BlockProperty<?>> accumulator) {
        long result = identity;
        for (RegisteredBlockProperty registry : byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result);
        }
        return result;
    }

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

    @RequiredArgsConstructor
    private static final class RegisteredBlockProperty {
        private final BlockProperty<?> property;
        private final int offset;

        @Override
        public String toString() {
            return offset+"-"+(offset+property.getBitSize())+":"+property.getName();
        }
    }
}
