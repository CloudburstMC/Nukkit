package cn.nukkit.blockstate;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.api.Unsigned;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.UnknownRuntimeIdException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.blockstate.exception.InvalidBlockStateDataTypeException;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.Validation;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
@ParametersAreNonnullByDefault
public final class BlockState implements Serializable, IBlockState {
    private static final long serialVersionUID = 623759888114628578L;

    private static final BigInteger SIXTEEN = BigInteger.valueOf(16);
    private static final BigInteger BYTE_LIMIT = BigInteger.valueOf(Byte.MAX_VALUE);
    private static final BigInteger INT_LIMIT = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger LONG_LIMIT = BigInteger.valueOf(Long.MAX_VALUE);
    
    private static final ZeroStorage ZERO_STORAGE = new ZeroStorage();
    
    @SuppressWarnings({"deprecation", "java:S1874"})
    private static final BlockState[][] STATES_COMMON = new BlockState[16][Block.MAX_BLOCK_ID];
    private static final ConcurrentMap<String, BlockState> STATES_UNCOMMON = new ConcurrentHashMap<>();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockState AIR = BlockState.of(BlockID.AIR, 0);
    
    private static BlockState growCommonPool(@Nonnegative int blockId, @Nonnegative byte blockData) {
        synchronized (STATES_COMMON) {
            BlockState[] blockIds = STATES_COMMON[blockData];
            int newLen = blockId + 1;
            if (blockIds.length < newLen) {
                STATES_COMMON[blockData] = blockIds = Arrays.copyOf(blockIds, blockId + 1);
            }
            BlockState state = new BlockState(blockId, blockData);
            blockIds[blockId] = state;
            return state;
        }
    }
    
    private static BlockState of0xF(@Nonnegative int blockId, @Nonnegative byte blockData) {
        BlockState[] blockIds = STATES_COMMON[blockData];
        if (blockIds.length <= blockId) {
            return growCommonPool(blockId, blockData);
        }
        
        BlockState state = blockIds[blockId];
        if (state != null) {
            return state;
        }
        
        BlockState newState = new BlockState(blockId, blockData);
        blockIds[blockId] = newState;
        return newState;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(@Nonnegative int blockId) {
        return of0xF(blockId, (byte)0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(@Nonnegative int blockId, @Nonnegative byte blockData) {
        Validation.checkPositive("blockData", blockData);
        if (blockData < 16) {
            return of0xF(blockId, blockData);
        }
        return STATES_UNCOMMON.computeIfAbsent(blockId+":"+blockData, k-> new BlockState(blockId, blockData));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(@Nonnegative int blockId, @Nonnegative int blockData) {
        Validation.checkPositive("blockData", blockData);
        if (blockData < 16) {
            return of0xF(blockId, (byte)blockData);
        }
        return STATES_UNCOMMON.computeIfAbsent(blockId+":"+blockData, k-> new BlockState(blockId, blockData));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(@Nonnegative int blockId, @Nonnegative long blockData) {
        Validation.checkPositive("blockData", blockData);
        if (blockData < 16) {
            return of0xF(blockId, (byte)blockData);
        }
        return STATES_UNCOMMON.computeIfAbsent(blockId+":"+blockData, k-> new BlockState(blockId, blockData));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(@Nonnegative int blockId, @Nonnegative BigInteger blockData) {
        Validation.checkPositive("blockData", blockData);
        if (blockData.compareTo(SIXTEEN) < 0) {
            return of0xF(blockId, blockData.byteValue());
        }
        return STATES_UNCOMMON.computeIfAbsent(blockId+":"+blockData, k-> new BlockState(blockId, blockData));
    }

    /**
     * @throws InvalidBlockStateDataTypeException If the {@code blockData} param is not {@link Integer}, {@link Long},
     * or {@link BigInteger}.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(@Nonnegative int blockId, @Nonnegative Number blockData) {
        Class<? extends Number> c = blockData.getClass();
        if (c == Byte.class) {
            return of(blockId, blockData.byteValue()); 
        } else if (c == Integer.class) {
            return of(blockId, blockData.intValue());
        } else if (c == Long.class) {
            return of(blockId, blockData.longValue());
        } else if (c == BigInteger.class) {
            return of(blockId, (BigInteger) blockData);
        } else {
            throw new InvalidBlockStateDataTypeException(blockData);
        }
    }
    
    @Getter
    @Nonnegative
    private final int blockId;
    
    @Nonnull
    @Nonnegative
    private final Storage storage;
    
    @ToString.Exclude
    @Nonnull
    private OptionalBoolean valid = OptionalBoolean.empty();

    private BlockState(@Nonnegative int blockId) {
        Validation.checkPositive("blockId", blockId);
        this.blockId = blockId;
        storage = ZERO_STORAGE;
    }

    private BlockState(@Nonnegative int blockId, @Nonnegative byte blockData) {
        Validation.checkPositive("blockId", blockId);
        this.blockId = blockId;
        storage = blockData == 0?   ZERO_STORAGE : 
                                    new ByteStorage(blockData);
    }
    
    private BlockState(@Nonnegative int blockId, @Nonnegative int blockData) {
        Validation.checkPositive("blockId", blockId);
        this.blockId = blockId;
        storage = blockData == 0?               ZERO_STORAGE : 
                //blockData < 0?                  new IntStorage(blockData) :
                blockData <= Byte.MAX_VALUE?    new ByteStorage((byte)blockData) : 
                                                new IntStorage(blockData);
    }
    
    private BlockState(@Nonnegative int blockId, @Nonnegative long blockData) {
        Validation.checkPositive("blockId", blockId);
        this.blockId = blockId;
        storage = blockData == 0?               ZERO_STORAGE : 
                //blockData < 0?                  new LongStorage(blockData) :
                blockData <= Byte.MAX_VALUE?    new ByteStorage((byte)blockData) :
                blockData <= Integer.MAX_VALUE? new IntStorage((int)blockData) : 
                                                new LongStorage(blockData);
    }
    
    private BlockState(@Nonnegative int blockId, @Nonnegative BigInteger blockData) {
        Validation.checkPositive("blockId", blockId);
        this.blockId = blockId;
        int zeroCmp = BigInteger.ZERO.compareTo(blockData);
        if (zeroCmp == 0) {
            storage = ZERO_STORAGE;
        //} else if (zeroCmp < 0) {
        //    storage = new BigIntegerStorage(blockData);
        } else if (blockData.compareTo(BYTE_LIMIT) <= 0) {
            storage = new ByteStorage(blockData.byteValue());
        } else if (blockData.compareTo(INT_LIMIT) <= 0) {
            storage = new IntStorage(blockData.intValue());
        } else if (blockData.compareTo(LONG_LIMIT) <= 0) {
            storage = new LongStorage(blockData.longValue());
        } else {
            storage = new BigIntegerStorage(blockData);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockState withData(@Nonnegative int data) {
        return of(blockId, data);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockState withData(@Nonnegative long data) {
        return of(blockId, data);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockState withData(@Nonnegative BigInteger data) {
        return of(blockId, data);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockState withData(@Nonnegative Number data) {
        return of(blockId, data);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockState withBlockId(@Nonnegative int blockId) {
        return storage.withBlockId(blockId);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public <E extends Serializable> BlockState withProperty(BlockProperty<E> property, @Nullable E value) {
        return withProperty(property.getName(), value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockState withProperty(String propertyName, @Nullable Serializable value) {
        return storage.withProperty(blockId, getProperties(), propertyName, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockState withProperty(String propertyName, String persistenceValue) {
        return storage.withPropertyString(blockId, getProperties(), propertyName, persistenceValue);
    }

    /**
     * @throws NoSuchElementException If any of the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState onlyWithProperties(BlockProperty<?>... properties) {
        String[] names = new String[properties.length];
        for (int i = 0; i < properties.length; i++) {
            names[i] = properties[i].getName();
        }
        return onlyWithProperties(names);
    }

    /**
     * @throws NoSuchElementException If any of the given property names is not found
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState onlyWithProperties(String... propertyNames) {
        BlockProperties properties = getProperties();
        List<String> list = Arrays.asList(propertyNames);
        if (!properties.getNames().containsAll(list)) {
            Set<String> missing = new LinkedHashSet<>(list);
            missing.removeAll(properties.getNames());
            throw new NoSuchElementException("Missing properties: " + String.join(", ", missing));
        }
        
        return storage.onlyWithProperties(this, list);
    }

    /**
     * @throws NoSuchElementException If the property was not found
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState onlyWithProperty(String name) {
        return onlyWithProperties(name);
    }

    /**
     * @throws NoSuchElementException If the property was not found
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState onlyWithProperty(BlockProperty<?> property) {
        return onlyWithProperties(property);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState onlyWithProperty(String name, Serializable value) {
        return storage.onlyWithProperty(this, name, value);
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public <T extends Serializable> BlockState onlyWithProperty(BlockProperty<T> property, T value) {
        return onlyWithProperty(property.getName(), value);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockState forItem() {
        BlockProperties allProperties = getProperties();
        Set<String> allNames = allProperties.getNames();
        
        BlockProperties itemProperties = allProperties.getItemBlockProperties();
        List<String> itemNames = itemProperties.getItemPropertyNames();
        if (allNames.size() == itemNames.size() && allNames.containsAll(itemNames)) {
            return this;
        }
        return storage.onlyWithProperties(this, itemNames);
    }


    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public ItemBlock asItemBlock(int count) {
        BlockProperties allProperties = getProperties();
        Set<String> allNames = allProperties.getNames();
        
        int itemBlockMeta;
        BlockProperties itemProperties = allProperties.getItemBlockProperties();
        List<String> itemNames = itemProperties.getItemPropertyNames();
        BlockState trimmedState;
        if (allNames.size() == itemNames.size() && allNames.containsAll(itemNames)) {
            itemBlockMeta = getExactIntStorage();
            trimmedState = this;
        } else if (itemNames.isEmpty()) {
            itemBlockMeta = 0; 
            trimmedState = isDefaultState()? this : of(getBlockId());
        } else {
            trimmedState = storage.onlyWithProperties(this, itemNames);
            MutableBlockState itemState = itemProperties.createMutableState(getBlockId());
            itemNames.forEach(property -> itemState.setPropertyValue(property, getPropertyValue(property)));
            itemBlockMeta = itemState.getExactIntStorage();
        }
        
        int runtimeId = trimmedState.getRuntimeId();
        if (runtimeId == BlockStateRegistry.getUpdateBlockRegistration() && !"minecraft:info_update".equals(trimmedState.getPersistenceName())) {
            throw new UnknownRuntimeIdException("The current block state can't be represented as an item. State: "+trimmedState+", Trimmed: "+trimmedState+" ItemBlockMeta: "+itemBlockMeta);
        }
        Block block = trimmedState.getBlock();
        return new ItemBlock(block, itemBlockMeta, count);
    }

    @Nonnegative
    @Nonnull
    @Override
    public Number getDataStorage() {
        return storage.getNumber();
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return BlockStateRegistry.getProperties(blockId);
    }

    @Nonnegative
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getLegacyDamage() {
        return storage.getLegacyDamage();
    }

    @Unsigned
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getBigDamage() {
        return storage.getBigDamage();
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getSignedBigDamage() {
        return storage.getSignedBigDamage();
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public BigInteger getHugeDamage() {
        return storage.getHugeDamage();
    }

    @Nonnull
    @Override
    public Serializable getPropertyValue(String propertyName) {
        return storage.getPropertyValue(getProperties(), propertyName);
    }

    @Override
    public int getIntValue(String propertyName) {
        return storage.getIntValue(getProperties(), propertyName);
    }

    @Override
    public boolean getBooleanValue(String propertyName) {
        return storage.getBooleanValue(getProperties(), propertyName);
    }

    @Nonnull
    @Override
    public String getPersistenceValue(String propertyName) {
        return storage.getPersistenceValue(getProperties(), propertyName);
    }

    @Nonnull
    @Override
    public BlockState getCurrentState() {
        return this;
    }

    @Override
    public int getBitSize() {
        return storage.getBitSize();
    }

    /**
     * @throws ArithmeticException If the storage have more than 32 bits
     */
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getExactIntStorage() {
        Class<? extends Storage> storageClass = storage.getClass();
        if (getBitSize() >= 32 || storageClass != ZeroStorage.class && storageClass != ByteStorage.class && storageClass != IntStorage.class) {
            throw new ArithmeticException(getDataStorage()+" cant be stored in a signed 32 bits integer without losses. It has "+getBitSize()+" bits");
        }
        return getSignedBigDamage();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultState() {
        return storage.isDefaultState();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockState that = (BlockState) o;

        if (blockId != that.blockId) return false;
        if (storage.getBitSize() != that.storage.getBitSize()) return false;
        return compareDataEquality(storage.getNumber(), that.storage.getNumber());
    }

    @Override
    public int hashCode() {
        int bitSize = storage.getBitSize();
        int result = blockId;
        result = 31 * result + bitSize;
        if (bitSize <= 32) {
            result = 31 * result + storage.getBigDamage();
        } else if (bitSize <= 64) {
            result = 31 * result + Long.hashCode(storage.getNumber().longValue());
        } else {
            result = 31 * result + storage.getHugeDamage().hashCode();
        }
        return result;
    }


    private static boolean compareDataEquality(Number a, Number b) {
        Class<? extends Number> aClass = a.getClass();
        Class<? extends Number> bClass = b.getClass();
        if (aClass == bClass) {
            return a.equals(b);
        }
        if (aClass != BigInteger.class && bClass != BigInteger.class) {
            return a.longValue() == b.longValue();
        }
        
        BigInteger aBig = aClass == BigInteger.class? (BigInteger) a : new BigInteger(a.toString());
        BigInteger bBig = bClass == BigInteger.class? (BigInteger) b : new BigInteger(b.toString());
        return aBig.equals(bBig);
    }

    /**
     * @throws InvalidBlockStateException If the stored state is invalid
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void validate() {
        if (valid == OptionalBoolean.TRUE) {
            return;
        }
        
        BlockProperties properties = getProperties();
        if (storage.getBitSize() > properties.getBitSize()) {
            throw new InvalidBlockStateException(this, 
                    "The stored data overflows the maximum properties bits. Stored bits: "+storage.getBitSize()+", " +
                            "Properties Bits: "+properties.getBitSize()+", Stored data: "+storage.getNumber()
            );
        }
        
        try {
            storage.validate(properties);
            valid = OptionalBoolean.TRUE;
        } catch (Exception e) {
            valid = OptionalBoolean.FALSE;
            throw new InvalidBlockStateException(this, e);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isCachedValidationValid() {
        return valid.orElse(false);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public OptionalBoolean getCachedValidation() {
        return valid;
    }

    @Nonnull
    @Override
    public Block getBlock() {
        try {
            Block block = IBlockState.super.getBlock();
            valid = OptionalBoolean.TRUE;
            return block;
        } catch (InvalidBlockStateException e) {
            valid = OptionalBoolean.FALSE;
            throw e;
        }
    }

    @Nonnull
    @Override
    public Block getBlock(@Nullable Level level, int x, int y, int z, int layer, boolean repair, @Nullable Consumer<BlockStateRepair> callback) {
        if (valid == OptionalBoolean.TRUE) {
            Block block = IBlockState.super.getBlock();
            block.x = x;
            block.y = y;
            block.z = z;
            block.layer = layer;
            block.level = level;
            return block;
        }
        
        if (valid == OptionalBoolean.FALSE) {
            return IBlockState.super.getBlock(level, x, y, z, layer, repair, callback);
        }
        
        Consumer<BlockStateRepair> updater = r-> valid = OptionalBoolean.FALSE;
        
        if (repair && callback != null) {
            callback = updater.andThen(callback);
        } else {
            callback = updater.andThen(rep -> {
                throw new InvalidBlockStateException(this, "Attempted to repair when repair was false. "+rep.toString(), rep.getValidationException());
            });
        }
        
        try {
            Block block = IBlockState.super.getBlock(level, x, y, z, layer, true, callback);
            if (valid == OptionalBoolean.EMPTY) {
                valid = OptionalBoolean.TRUE;
            }
            return block;
        } catch (InvalidBlockStateException e) {
            valid = OptionalBoolean.FALSE;
            throw e;
        }
    }

    @ParametersAreNonnullByDefault
    private interface Storage extends Serializable {
        @Nonnull
        Number getNumber();

        int getLegacyDamage();

        int getBigDamage();
        
        default int getSignedBigDamage() {
            return getBigDamage();
        }

        @Nonnull
        Serializable getPropertyValue(BlockProperties properties, String propertyName);

        int getIntValue(BlockProperties properties, String propertyName);

        boolean getBooleanValue(BlockProperties properties, String propertyName);

        @Nonnull
        BlockState withBlockId(int blockId);

        @Nonnull
        String getPersistenceValue(BlockProperties properties, String propertyName);

        int getBitSize();

        @Nonnull
        BigInteger getHugeDamage();

        @Nonnull
        BlockState withProperty(int blockId, BlockProperties properties, String propertyName, @Nullable Serializable value);

        @Nonnull
        BlockState onlyWithProperties(BlockState currentState, List<String> propertyNames);

        @Nonnull
        BlockState onlyWithProperty(BlockState currentState, String name, Serializable value);

        void validate(BlockProperties properties);

        boolean isDefaultState();

        @Nonnull
        BlockState withPropertyString(int blockId, BlockProperties properties, String propertyName, String value);
    }

    @ParametersAreNonnullByDefault
    private static class ZeroStorage implements Storage {
        private static final long serialVersionUID = -4199347838375711088L;

        @Override
        public int getBitSize() {
            return 1;
        }

        @Nonnull
        @Override
        public Integer getNumber() {
            return 0;
        }

        @Override
        public int getLegacyDamage() {
            return 0;
        }

        @Override
        public int getBigDamage() {
            return 0;
        }

        @Nonnull
        @Override
        public BigInteger getHugeDamage() {
            return BigInteger.ZERO;
        }

        @Nonnull
        @Override
        public Serializable getPropertyValue(BlockProperties properties, String propertyName) {
            return properties.getValue(0, propertyName);
        }

        @Override
        public int getIntValue(BlockProperties properties, String propertyName) {
            return properties.getIntValue(0, propertyName);
        }

        @Override
        public boolean getBooleanValue(BlockProperties properties, String propertyName) {
            return properties.getBooleanValue(0, propertyName);
        }

        @Nonnull
        @Override
        public BlockState withBlockId(int blockId) {
            return BlockState.of(blockId);
        }

        @Nonnull
        @Override
        public BlockState withProperty(int blockId, BlockProperties properties, String propertyName, @Nullable Serializable value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId, properties.setValue(0, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState withPropertyString(int blockId, BlockProperties properties, String propertyName, String value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId, properties.setPersistenceValue(0, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState onlyWithProperties(BlockState currentState, List<String> propertyNames) {
            return currentState;
        }

        @Nonnull
        @Override
        public BlockState onlyWithProperty(BlockState currentState, String name, Serializable value) {
            BlockProperties properties = currentState.getProperties();
            if (!properties.contains(name)) {
                return currentState;
            }
            return BlockState.of(currentState.blockId, properties.setValue(0, name, value));
        }

        @Override
        public void validate(BlockProperties properties) {
            // Meta 0 is always valid
        }

        @Override
        public boolean isDefaultState() {
            return true;
        }

        @Nonnull
        @Override
        public String getPersistenceValue(BlockProperties properties, String propertyName) {
            return properties.getPersistenceValue(0, propertyName);
        }

        @Override
        public String toString() {
            return "0";
        }
    }
    
    private class ByteStorage implements Storage {
        private final byte data;

        @Getter
        private final int bitSize;

        public ByteStorage(byte data) {
            this.data = data;
            this.bitSize = NukkitMath.bitLength(data);
        }

        @Nonnull
        @Override
        public Number getNumber() {
            return data;
        }

        @Override
        public int getLegacyDamage() {
            return data & Block.DATA_MASK;
        }

        @Override
        public int getBigDamage() {
            return data;
        }

        @Nonnull
        @Override
        public BigInteger getHugeDamage() {
            return BigInteger.valueOf(data);
        }

        @Nonnull
        @Override
        public Serializable getPropertyValue(BlockProperties properties, String propertyName) {
            return properties.getValue(data, propertyName);
        }

        @Override
        public int getIntValue(BlockProperties properties, String propertyName) {
            return properties.getIntValue(data, propertyName);
        }

        @Override
        public boolean getBooleanValue(BlockProperties properties, String propertyName) {
            return properties.getBooleanValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BlockState withBlockId(int blockId) {
            return BlockState.of(blockId, data);
        }

        @Nonnull
        @Override
        public BlockState withProperty(int blockId, BlockProperties properties, String propertyName, @Nullable Serializable value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId, properties.setValue(data, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState withPropertyString(int blockId, BlockProperties properties, String propertyName, String value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId, properties.setPersistenceValue(data, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState onlyWithProperties(BlockState currentState, List<String> propertyNames) {
            return BlockState.of(blockId,
                    getProperties().reduceInt(data, (property, offset, current) ->
                            propertyNames.contains(property.getName())? current : property.setValue(current, offset, null)
                    )
            );
        }

        @Nonnull
        @Override
        @SuppressWarnings({"unchecked", "java:S1905", "rawtypes"})
        public BlockState onlyWithProperty(BlockState currentState, String name, Serializable value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId,
                    getProperties().reduceInt(data, (property, offset, current) ->
                            ((BlockProperty)property).setValue(current, offset, name.equals(property.getName())? value : null)
                    )
            );
        }

        @Override
        public void validate(BlockProperties properties) {
            properties.forEach((property, offset) -> property.validateMeta(data, offset));
        }

        @Override
        public boolean isDefaultState() {
            return data == 0;
        }

        @Nonnull
        @Override
        public String getPersistenceValue(BlockProperties properties, String propertyName) {
            return properties.getPersistenceValue(data, propertyName);
        }

        @Override
        public String toString() {
            return Byte.toString(data);
        }
    }
    
    @ParametersAreNonnullByDefault
    private class IntStorage implements Storage {
        private static final long serialVersionUID = 4700387399339051513L;
        private final int data;
        
        @Getter
        private final int bitSize;

        public IntStorage(int data) {
            this.data = data;
            bitSize = NukkitMath.bitLength(data);
        }

        @Nonnull
        @Override
        public Number getNumber() {
            return getBigDamage();
        }

        @Override
        public int getLegacyDamage() {
            return (data & Block.DATA_MASK);
        }

        @Override
        public int getBigDamage() {
            return data;
        }

        @Nonnull
        @Override
        public Serializable getPropertyValue(BlockProperties properties, String propertyName) {
            return properties.getValue(data, propertyName);
        }

        @Override
        public int getIntValue(BlockProperties properties, String propertyName) {
            return properties.getIntValue(data, propertyName);
        }

        @Override
        public boolean getBooleanValue(BlockProperties properties, String propertyName) {
            return properties.getBooleanValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BlockState withBlockId(int blockId) {
            return BlockState.of(blockId, data);
        }

        @Nonnull
        @Override
        public BlockState withProperty(int blockId, BlockProperties properties, String propertyName, @Nullable Serializable value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId, properties.setValue(data, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState withPropertyString(int blockId, BlockProperties properties, String propertyName, String value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId, properties.setPersistenceValue(data, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState onlyWithProperties(BlockState currentState, List<String> propertyNames) {
            return BlockState.of(blockId,
                getProperties().reduceInt(data, (property, offset, current) -> 
                        propertyNames.contains(property.getName())? current : property.setValue(current, offset, null)
                )
            );
        }

        @Nonnull
        @Override
        @SuppressWarnings({"unchecked", "java:S1905", "rawtypes"})
        public BlockState onlyWithProperty(BlockState currentState, String name, Serializable value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId,
                    getProperties().reduceInt(data, (property, offset, current) ->
                            ((BlockProperty)property).setValue(current, offset, name.equals(property.getName())? value : null)
                    )
            );
        }

        @Override
        public void validate(BlockProperties properties) {
            properties.forEach((property, offset) -> property.validateMeta(data, offset));
        }

        @Override
        public boolean isDefaultState() {
            return data == 0;
        }

        @Nonnull
        @Override
        public String getPersistenceValue(BlockProperties properties, String propertyName) {
            return properties.getPersistenceValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BigInteger getHugeDamage() {
            return BigInteger.valueOf(data);
        }

        @Override
        public String toString() {
            return Integer.toString(data);
        }
    }
    
    @ParametersAreNonnullByDefault
    private class LongStorage implements Storage {
        private static final long serialVersionUID = -2633333569914851875L;
        private final long data;
        
        @Getter
        private final int bitSize;

        public LongStorage(long data) {
            this.data = data;
            bitSize = NukkitMath.bitLength(data);
        }

        @Nonnull
        @Override
        public Number getNumber() {
            return data;
        }

        @Override
        public int getLegacyDamage() {
            return (int)(data & Block.DATA_MASK);
        }

        @Override
        public int getBigDamage() {
            return (int)(data & BlockStateRegistry.BIG_META_MASK);
        }

        @Override
        public int getSignedBigDamage() {
            return (int)(data & Integer.MAX_VALUE);
        }

        @Nonnull
        @Override
        public Serializable getPropertyValue(BlockProperties properties, String propertyName) {
            return properties.getValue(data, propertyName);
        }

        @Override
        public int getIntValue(BlockProperties properties, String propertyName) {
            return properties.getIntValue(data, propertyName);
        }

        @Override
        public boolean getBooleanValue(BlockProperties properties, String propertyName) {
            return properties.getBooleanValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BlockState withPropertyString(int blockId, BlockProperties properties, String propertyName, String value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId, properties.setPersistenceValue(data, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState withBlockId(int blockId) {
            return BlockState.of(blockId, data);
        }

        @Nonnull
        @Override
        public BlockState withProperty(int blockId, BlockProperties properties, String propertyName, @Nullable Serializable value) {
            return BlockState.of(blockId, properties.setValue(data, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState onlyWithProperties(BlockState currentState, List<String> propertyNames) {
            return BlockState.of(blockId,
                    getProperties().reduceLong(data, (property, offset, current) ->
                            propertyNames.contains(property.getName())? current : property.setValue(current, offset, null)
                    )
            );
        }

        @Nonnull
        @Override
        @SuppressWarnings({"unchecked", "java:S1905", "rawtypes"})
        public BlockState onlyWithProperty(BlockState currentState, String name, Serializable value) {
            // TODO This can cause problems when setting a property that increases the bit size
            return BlockState.of(blockId,
                    getProperties().reduceLong(data, (property, offset, current) ->
                            ((BlockProperty)property).setValue(current, offset, name.equals(property.getName())? value : null)
                    )
            );
        }

        @Override
        public void validate(BlockProperties properties) {
            properties.forEach((property, offset) -> property.validateMeta(data, offset));
        }

        @Override
        public boolean isDefaultState() {
            return data == 0;
        }

        @Nonnull
        @Override
        public String getPersistenceValue(BlockProperties properties, String propertyName) {
            return properties.getPersistenceValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BigInteger getHugeDamage() {
            return BigInteger.valueOf(data);
        }

        @Override
        public String toString() {
            return Long.toString(data);
        }
        
    }
    
    @ParametersAreNonnullByDefault
    private class BigIntegerStorage implements Storage {
        private static final long serialVersionUID = 2504213066240296662L;
        private final BigInteger data;

        @Getter
        private final int bitSize;

        public BigIntegerStorage(BigInteger data) {
            this.data = data;
            bitSize = NukkitMath.bitLength(data);
        }

        @Nonnull
        @Override
        public Number getNumber() {
            return getHugeDamage();
        }

        @Override
        public int getLegacyDamage() {
            return data.and(BigInteger.valueOf(Block.DATA_MASK)).intValue();
        }

        @Override
        public int getBigDamage() {
            return data.and(BigInteger.valueOf(BlockStateRegistry.BIG_META_MASK)).intValue();
        }

        @Override
        public int getSignedBigDamage() {
            return data.and(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
        }

        @Nonnull
        @Override
        public Serializable getPropertyValue(BlockProperties properties, String propertyName) {
            return properties.getValue(data, propertyName);
        }

        @Override
        public int getIntValue(BlockProperties properties, String propertyName) {
            return properties.getIntValue(data, propertyName);
        }

        @Override
        public boolean getBooleanValue(BlockProperties properties, String propertyName) {
            return properties.getBooleanValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BlockState withBlockId(int blockId) {
            return BlockState.of(blockId, data);
        }

        @Nonnull
        @Override
        public BlockState withProperty(int blockId, BlockProperties properties, String propertyName, @Nullable Serializable value) {
            return BlockState.of(blockId, properties.setValue(data, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState withPropertyString(int blockId, BlockProperties properties, String propertyName, String value) {
            return BlockState.of(blockId, properties.setPersistenceValue(data, propertyName, value));
        }

        @Nonnull
        @Override
        public BlockState onlyWithProperties(BlockState currentState, List<String> propertyNames) {
            return BlockState.of(blockId,
                    getProperties().reduce(data, (property, offset, current) ->
                            propertyNames.contains(property.getName())? current : property.setValue(current, offset, null)
                    )
            );
        }

        @Nonnull
        @Override
        @SuppressWarnings({"unchecked", "java:S1905", "rawtypes"})
        public BlockState onlyWithProperty(BlockState currentState, String name, Serializable value) {
            return BlockState.of(blockId,
                    getProperties().reduce(data, (property, offset, current) ->
                            ((BlockProperty)property).setValue(current, offset, name.equals(property.getName())? value : null)
                    )
            );
        }

        @Override
        public void validate(BlockProperties properties) {
            properties.forEach((property, offset) -> property.validateMeta(data, offset));
        }

        @Override
        public boolean isDefaultState() {
            return data.equals(BigInteger.ZERO);
        }

        @Nonnull
        @Override
        public String getPersistenceValue(BlockProperties properties, String propertyName) {
            return properties.getPersistenceValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BigInteger getHugeDamage() {
            return data;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }
}
