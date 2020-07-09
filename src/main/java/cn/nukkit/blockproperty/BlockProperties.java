package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BigIntegerMutableBlockState;
import cn.nukkit.blockstate.IntMutableBlockState;
import cn.nukkit.blockstate.LongMutableBlockState;
import cn.nukkit.blockstate.MutableBlockState;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        for (BlockProperty<?> property : properties) {
            Preconditions.checkArgument(property != null, "The properties can not contains null values");

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
    
    @SuppressWarnings("rawtypes")
    public BlockProperty getBlockProperty(String propertyName) {
        return getRegisteredProperty(propertyName).blockProperty;
    }

    public <T extends BlockProperty<?>> T getBlockProperty(String propertyName, Class<T> tClass) {
        return tClass.cast(getRegisteredProperty(propertyName).blockProperty);
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
        @SuppressWarnings("rawtypes") 
        BlockProperty unchecked = registry.blockProperty;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    public long setValue(long currentMeta, String propertyName, @Nullable Object value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        @SuppressWarnings("rawtypes")
        BlockProperty unchecked = registry.blockProperty;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    @Nonnull
    public BigInteger setValue(BigInteger currentMeta, String propertyName, @Nullable Object value) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        @SuppressWarnings("rawtypes")
        BlockProperty unchecked = registry.blockProperty;
        return unchecked.setValue(currentMeta, registry.offset, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Object getValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Object getValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Object getValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(int currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return tClass.cast(registry.blockProperty.getValue(currentMeta, registry.offset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(long currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return tClass.cast(registry.blockProperty.getValue(currentMeta, registry.offset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <T> T getCheckedValue(BigInteger currentMeta, String propertyName, Class<T> tClass) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return tClass.cast(registry.blockProperty.getValue(currentMeta, registry.offset));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return (T) registry.blockProperty.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return (T) registry.blockProperty.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getUncheckedValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return (T) registry.blockProperty.getValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getIntValue(currentMeta, registry.offset);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getIntValue(currentMeta, registry.offset);
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getIntValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getIntValue(currentMeta, registry.offset);
    }
    
    public String getPersistenceValue(int currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getPersistenceValue(currentMeta, registry.offset);
    }

    public String getPersistenceValue(long currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getPersistenceValue(currentMeta, registry.offset);
    }

    public String getPersistenceValue(BigInteger currentMeta, String propertyName) {
        RegisteredBlockProperty registry = getRegisteredProperty(propertyName);
        return registry.blockProperty.getPersistenceValue(currentMeta, registry.offset);
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

    public void forEach(BiConsumer<BlockProperty<?>, Integer> consumer) {
        for (RegisteredBlockProperty registry : byName.values()) {
            consumer.accept(registry.blockProperty, registry.offset);
        }
    }
    
    public void forEach(Consumer<BlockProperty<?>> consumer) {
        for (RegisteredBlockProperty registry : byName.values()) {
            consumer.accept(registry.blockProperty);
        }
    }
    
    @RequiredArgsConstructor
    private static class RegisteredBlockProperty {
        private final BlockProperty<?> blockProperty;
        private final int offset;
    }
}
