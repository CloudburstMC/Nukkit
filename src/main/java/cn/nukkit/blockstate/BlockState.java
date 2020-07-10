package cn.nukkit.blockstate;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.math.NukkitMath;
import com.google.common.collect.MapMaker;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentMap;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
@ParametersAreNonnullByDefault
public final class BlockState implements Serializable, IBlockState {
    private static final ConcurrentMap<String, BlockState> STATES = new MapMaker().weakValues().makeMap();
    public static final BlockState AIR = BlockState.of(BlockID.AIR, 0);
    private static final BigInteger INT_MASK = BigInteger.valueOf(0xFFFFFFFFL);
    private static final BigInteger LONG_MASK = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(int blockId) {
        return STATES.computeIfAbsent(blockId+":0", k-> new BlockState(blockId));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(int blockId, int blockData) {
        return STATES.computeIfAbsent(blockId+":"+blockData, k-> new BlockState(blockId, blockData));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(int blockId, long blockData) {
        return STATES.computeIfAbsent(blockId+":"+blockData, k-> new BlockState(blockId, blockData));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(int blockId, BigInteger blockData) {
        return STATES.computeIfAbsent(blockId+":"+blockData, k-> new BlockState(blockId, blockData));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static BlockState of(int blockId, Number blockData) {
        return STATES.computeIfAbsent(blockId+":"+blockData, l-> {
            if (blockData instanceof Integer) {
                return new BlockState(blockId, blockData.intValue());
            } else if (blockData instanceof Long) {
                return new BlockState(blockId, blockData.longValue());
            } else if (blockData instanceof BigInteger) {
                return new BlockState(blockId, (BigInteger) blockData);
            } else {
                throw new IllegalArgumentException("The block data " + blockData + " has an unsupported type " + blockData.getClass());
            }
        });
    }
    
    @Getter
    private final int blockId;
    @Nonnull
    private final Storage storage;

    private BlockState(int blockId) {
        this(blockId, 0);
    }
    
    private BlockState(int blockId, int blockData) {
        this.blockId = blockId;
        storage = new IntStorage(blockData);
    }
    
    private BlockState(int blockId, long blockData) {
        this.blockId = blockId;
        storage = blockData <= 0xFFFFFFFFL? new IntStorage((int)blockData) : new LongStorage(blockData);
    }
    
    private BlockState(int blockId, BigInteger blockData) {
        this.blockId = blockId;
        if (blockData.compareTo(INT_MASK) < 0) {
            storage = new IntStorage(blockData.intValue());
        } else if (blockData.compareTo(LONG_MASK) < 0) {
            storage = new LongStorage(blockData.longValue());
        } else {
            storage = new BigIntegerStorage(blockData);
        }
    }
    
    @Nonnull
    public BlockState withData(int data) {
        return of(blockId, data);
    }

    @Nonnull
    public BlockState withData(long data) {
        return of(blockId, data);
    }

    @Nonnull
    public BlockState withData(BigInteger data) {
        return of(blockId, data);
    }

    @Nonnull
    public BlockState withData(Number data) {
        return of(blockId, data);
    }

    @Nonnull
    public BlockState withBlockId(int blockId) {
        return storage.withBlockId(blockId);
    }

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

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getLegacyDamage() {
        return storage.getLegacyDamage();
    }

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getBigDamage() {
        return storage.getBigDamage();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public BigInteger getHugeDamage() {
        return storage.getHugeDamage();
    }

    @Nonnull
    @Override
    public Object getPropertyValue(String propertyName) {
        return storage.getPropertyValue(propertyName);
    }

    @Override
    public int getIntValue(String propertyName) {
        return storage.getIntValue(propertyName);
    }

    @Override
    public boolean getBooleanValue(String propertyName) {
        return storage.getBooleanValue(propertyName);
    }

    @Nonnull
    @Override
    public String getPersistenceValue(String propertyName) {
        return storage.getPersistenceValue(propertyName);
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
    
    @ParametersAreNonnullByDefault
    private interface Storage extends Serializable {
        @Nonnull
        Number getNumber();

        int getLegacyDamage();

        int getBigDamage();

        @Nonnull
        Object getPropertyValue(String propertyName);

        int getIntValue(String propertyName);

        boolean getBooleanValue(String propertyName);

        @Nonnull
        BlockState withBlockId(int blockId);

        @Nonnull
        String getPersistenceValue(String propertyName);

        int getBitSize();

        @Nonnull
        BigInteger getHugeDamage();
    }
    
    @ParametersAreNonnullByDefault
    private class IntStorage implements Storage {
        private final int data;
        
        @Getter
        @EqualsAndHashCode.Exclude
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
        public Object getPropertyValue(String propertyName) {
            return getProperties().getValue(data, propertyName);
        }

        @Override
        public int getIntValue(String propertyName) {
            return getProperties().getIntValue(data, propertyName);
        }

        @Override
        public boolean getBooleanValue(String propertyName) {
            return getProperties().getBooleanValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BlockState withBlockId(int blockId) {
            return BlockState.of(blockId, data);
        }

        @Nonnull
        @Override
        public String getPersistenceValue(String propertyName) {
            return getProperties().getPersistenceValue(data, propertyName);
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
        private final long data;
        
        @Getter
        @EqualsAndHashCode.Exclude
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

        @Nonnull
        @Override
        public Object getPropertyValue(String propertyName) {
            return getProperties().getValue(data, propertyName);
        }

        @Override
        public int getIntValue(String propertyName) {
            return getProperties().getIntValue(data, propertyName);
        }

        @Override
        public boolean getBooleanValue(String propertyName) {
            return getProperties().getBooleanValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BlockState withBlockId(int blockId) {
            return BlockState.of(blockId, data);
        }

        @Nonnull
        @Override
        public String getPersistenceValue(String propertyName) {
            return getProperties().getPersistenceValue(data, propertyName);
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
        private final BigInteger data;

        @Getter
        @EqualsAndHashCode.Exclude
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

        @Nonnull
        @Override
        public Object getPropertyValue(String propertyName) {
            return getProperties().getValue(data, propertyName);
        }

        @Override
        public int getIntValue(String propertyName) {
            return getProperties().getIntValue(data, propertyName);
        }

        @Override
        public boolean getBooleanValue(String propertyName) {
            return getProperties().getBooleanValue(data, propertyName);
        }

        @Nonnull
        @Override
        public BlockState withBlockId(int blockId) {
            return BlockState.of(blockId, data);
        }

        @Nonnull
        @Override
        public String getPersistenceValue(String propertyName) {
            return getProperties().getPersistenceValue(data, propertyName);
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
