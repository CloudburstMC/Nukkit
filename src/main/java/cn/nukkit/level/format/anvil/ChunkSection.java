package cn.nukkit.level.format.anvil;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.ImmutableBlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.format.updater.ChunkUpdater;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
@SuppressWarnings("java:S2176")
@ParametersAreNonnullByDefault
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    public static final int STREAM_STORAGE_VERSION = 8;
    public static final int SAVE_STORAGE_VERSION = 7;
    
    private static final String STORAGE_TAG_NAME = "Storage";
    private static final String HUGE_TAG_NAME = "DataHyper";
    private static final BigInteger BYTE_MASK = BigInteger.valueOf(0xFF);

    private final int y;

    private LayerStorage layerStorage;

    protected byte[] blockLight;
    protected byte[] skyLight;
    protected byte[] compressedLight;
    protected boolean hasBlockLight;
    protected boolean hasSkyLight;
    
    private int contentVersion;

    private ChunkSection(
            int y, LayerStorage layerStorage, @Nullable byte[] blockLight, @Nullable byte[] skyLight, 
            @Nullable byte[] compressedLight, boolean hasBlockLight, boolean hasSkyLight) {
        this.y = y;
        this.layerStorage = layerStorage;
        this.skyLight = skyLight;
        this.blockLight = blockLight;
        this.compressedLight = compressedLight;
        this.hasBlockLight = hasBlockLight;
        this.hasSkyLight = hasSkyLight;
    }

    public ChunkSection(int y) {
        this.y = y;
        this.contentVersion = ChunkUpdater.getCurrentContentVersion();
        layerStorage = LayerStorage.EMPTY;
        hasBlockLight = false;
        hasSkyLight = false;
    }

    public ChunkSection(CompoundTag nbt) {
        this.y = nbt.getByte("Y");
        
        setContentVersion(nbt.getByte("ContentVersion"));
        int version = nbt.getByte("Version");

        ListTag<CompoundTag> storageTagList = getStorageTagList(nbt, version);
        
        switch (storageTagList.size()) {
            case 0: layerStorage = LayerStorage.EMPTY; break;
            case 1: layerStorage = new SingleLayerStorage(); break;
            default: layerStorage = new MultiLayerStorage(ImmutableBlockStorage.EMPTY, ImmutableBlockStorage.EMPTY); break;
        }
        
        for (int i = 0; i < storageTagList.size(); i++) {
            CompoundTag storageTag = storageTagList.get(i);
            loadStorage(i, storageTag);
        }
        
        layerStorage.compress(this::setLayerStorage);

        this.blockLight = nbt.getByteArray("BlockLight");
        this.skyLight = nbt.getByteArray("SkyLight");
    }
    
    private void loadStorage(int layer, CompoundTag storageTag) {
        byte[] blocks = storageTag.getByteArray("Blocks");
        boolean hasBlockIds = false;
        if (blocks.length == 0) {
            blocks = EmptyChunkSection.EMPTY_ID_ARRAY;
        } else {
            hasBlockIds = true;
        }

        byte[] blocksExtra = storageTag.getByteArray("BlocksExtra");
        if (blocksExtra.length == 0) {
            blocksExtra = EmptyChunkSection.EMPTY_ID_ARRAY;
        }

        byte[] dataBytes = storageTag.getByteArray("Data");
        if (dataBytes.length == 0) {
            dataBytes = EmptyChunkSection.EMPTY_DATA_ARRAY;
        } else {
            hasBlockIds = true;
        }
        NibbleArray data = new NibbleArray(dataBytes);

        byte[] dataExtraBytes = storageTag.getByteArray("DataExtra");
        if (dataExtraBytes.length == 0) {
            dataExtraBytes = EmptyChunkSection.EMPTY_DATA_ARRAY;
        }
        NibbleArray dataExtra = new NibbleArray(dataExtraBytes);

        ListTag<ByteArrayTag> hugeDataList = storageTag.getList(HUGE_TAG_NAME, ByteArrayTag.class);
        int hugeDataSize = hugeDataList.size();

        if (!hasBlockIds && hugeDataSize == 0) {
            return;
        }

        if (getContentVersion() > ChunkUpdater.getCurrentContentVersion()) {
            log.warn(
                "Loading a chunk section with content version ("+getContentVersion()+") higher than the current version ("+ChunkUpdater.getCurrentContentVersion()+"), " +
                "Errors may occur and the chunk may get corrupted blocks!"
            );
        }
        
        BlockStorage storage = layerStorage.getOrSetStorage(this::setLayerStorage, this::getContentVersion, layer);
        
        // Convert YZX to XZY
        for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
                for (int by = 0; by < 16; by++) {
                    int index = getAnvilIndex(bx, by, bz);
                    int blockId = composeBlockId(blocks[index], blocksExtra[index]);
                    int composedData = composeData(data.get(index), dataExtra.get(index));
                    BlockState state = loadState(index, blockId, composedData, hugeDataList, hugeDataSize);
                    storage.setBlockState(bx, by, bz, state);
                }
            }
        }
    }
    
    private static BlockState loadState(int index, int blockId, int composedData, ListTag<ByteArrayTag> hugeDataList, int hugeDataSize) {
        if (hugeDataSize == 0) {
            return BlockState.of(blockId, composedData);
        } else if (hugeDataSize < 3) {
            return loadHugeIntData(index, blockId, composedData, hugeDataList, hugeDataSize);
        } else if (hugeDataSize < 7) {
            return loadHugeLongData(index, blockId, composedData, hugeDataList, hugeDataSize);
        } else {
            return loadHugeBigData(index, blockId, composedData, hugeDataList, hugeDataSize);
        }
    }

    private static BlockState loadHugeIntData(int index, int blockId, int composedData, ListTag<ByteArrayTag> hugeDataList, int hugeDataSize) {
        int data = composedData;
        for (int dataIndex = 0; dataIndex < hugeDataSize; dataIndex++) {
            int longPart = (hugeDataList.get(dataIndex).data[index] & 0xFF) << 8 << (8 * dataIndex);
            data |= longPart;
        }
        return BlockState.of(blockId, data);
    }

    private static BlockState loadHugeLongData(int index, int blockId, int composedData, ListTag<ByteArrayTag> hugeDataList, int hugeDataSize) {
        long data = composedData;
        for (int dataIndex = 0; dataIndex < hugeDataSize; dataIndex++) {
            long longPart = (hugeDataList.get(dataIndex).data[index] & 0xFFL) << 8 << (8 * dataIndex);
            data |= longPart;
        }
        return BlockState.of(blockId, data);
    }
    
    private static BlockState loadHugeBigData(int index, int blockId, int composedData, ListTag<ByteArrayTag> hugeDataList, int hugeDataSize) {
        BigInteger data = BigInteger.valueOf(composedData);
        for (int dataIndex = 0; dataIndex < hugeDataSize; dataIndex++) {
            BigInteger hugePart = BigInteger.valueOf((hugeDataList.get(dataIndex).data[index] & 0xFFL) << 8).shiftLeft(8 * dataIndex);
            data = data.or(hugePart);
        }
        return BlockState.of(blockId, data);
    }

    private static ListTag<CompoundTag> getStorageTagList(CompoundTag nbt, int version) {
        ListTag<CompoundTag> storageTagList;
        if (version == SAVE_STORAGE_VERSION || version == 8) {
            storageTagList = nbt.getList(STORAGE_TAG_NAME, CompoundTag.class);
        } else if (version == 0 || version == 1) {
            storageTagList = new ListTag<>(STORAGE_TAG_NAME);
            storageTagList.add(nbt);
        } else {
            throw new ChunkException("Unsupported chunk section version: " + version);
        }
        return storageTagList;
    }

    private static int composeBlockId(byte baseId, byte extraId) {
        return ((extraId & 0xFF) << 8) | (baseId & 0xFF);
    }

    private static int composeData(byte baseData, byte extraData) {
        return ((extraData & 0xF) << 4) | (baseData & 0xF);
    }

    private static int getAnvilIndex(int x, int y, int z) {
        return (y << 8) + (z << 4) + x; // YZX
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return getBlockId(x, y, z, 0);
    }

    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        return layerStorage.getStorageOrEmpty(layer).getBlockId(x, y, z);
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        setBlockId(x, y, z, 0, id);
    }

    @Override
    public synchronized void setBlockId(int x, int y, int z, int layer, int id) {
        if (id != 0) {
            layerStorage.getOrSetStorage(this::setLayerStorage, this::getContentVersion, layer).setBlockId(x, y, z, id);
        } else {
            BlockStorage storage = layerStorage.getStorageOrNull(layer);
            if (storage != null) {
                storage.setBlockId(x, y, z, id);
            }
        }
    }
    
    private void setLayerStorage(LayerStorage storage) {
        this.layerStorage = storage;
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        setFullBlockId(x, y, z, 0, fullId);
        return true;
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public synchronized boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        if (fullId != 0) {
            layerStorage.getOrSetStorage(this::setLayerStorage, this::getContentVersion, layer).setFullBlock(x, y, z, fullId);
        } else {
            BlockStorage storage = layerStorage.getStorageOrNull(layer);
            if (storage != null) {
                storage.setFullBlock(x, y, z, fullId);
            }
        }
        return true;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public int getBlockData(int x, int y, int z) {
        return getBlockData(x, y, z, 0);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return layerStorage.getStorageOrEmpty(layer).getBlockData(x, y, z);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public void setBlockData(int x, int y, int z, int data) {
        setBlockData(x, y, z, 0, data);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public synchronized void setBlockData(int x, int y, int z, int layer, int data) {
        if (data != 0) {
            layerStorage.getOrSetStorage(this::setLayerStorage, this::getContentVersion, layer).setBlockData(x, y, z, data);
        } else {
            BlockStorage storage = layerStorage.getStorageOrNull(layer);
            if (storage != null) {
                storage.setBlockData(x, y, z, data);
            }
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(x, y, z, 0);
    }

    @Nonnull
    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return layerStorage.getStorageOrEmpty(layer).getBlockState(x, y, z);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return layerStorage.getStorageOrEmpty(layer).getFullBlock(x, y, z);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return setBlockStateAtLayer(x, y, z, 0, BlockState.of(blockId));
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId));
    }

    @Nonnull
    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return getAndSetBlock(x, y, z, 0, block);
    }

    @Nonnull
    @Override
    public synchronized Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        BlockStorage storage;
        if (block.getId() != 0 || !block.isDefaultState()) {
            storage = layerStorage.getOrSetStorage(this::setLayerStorage, this::getContentVersion, layer);
        } else {
            storage = layerStorage.getStorageOrNull(layer);
            if (storage == null) {
                return BlockState.AIR.getBlock();
            }
        }
        
        BlockState state = storage.getAndSetBlockState(x, y, z, block.getCurrentState());
        try {
            return state.getBlock();
        } catch (InvalidBlockStateException ignored) {
            return new BlockUnknown(state.getBlockId(), state.getExactIntStorage());
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public synchronized BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state) {
        if (!BlockState.AIR.equals(state)) {
            return layerStorage.getOrSetStorage(this::setLayerStorage, this::getContentVersion, layer).getAndSetBlockState(x, y, z, state);
        } else {
            BlockStorage storage = layerStorage.getStorageOrNull(layer);
            if (storage == null) {
                return BlockState.AIR;
            }
            return storage.getAndSetBlockState(x, y, z, state);
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return setBlockAtLayer(x, y, z, 0, blockId, meta);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId, meta));
    }

    @Override
    public synchronized boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state) {
        BlockState previous = getAndSetBlockState(x, y, z, layer, state);
        return !state.equals(previous);
    }

    @Override
    public int getBlockChangeStateAbove(int x, int y, int z) {
        BlockStorage storage = layerStorage.getStorageOrNull(0);
        if (storage == null) {
            return 0;
        }
        return storage.getBlockChangeStateAbove(x, y, z);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public synchronized void delayPaletteUpdates() {
        layerStorage.delayPaletteUpdates();
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        if (this.skyLight == null) {
            if (!hasSkyLight) {
                return 0;
            } else if (compressedLight == null) {
                return 15;
            }
        }
        this.skyLight = getSkyLightArray();
        int sl = this.skyLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return sl & 0x0f;
        }
        return sl >> 4;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        if (this.skyLight == null) {
            if (hasSkyLight && compressedLight != null) {
                this.skyLight = getSkyLightArray();
            } else if (level == (hasSkyLight ? 15 : 0)) {
                return;
            } else {
                this.skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(this.skyLight, (byte) 0xFF);
                }
            }
        }
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.skyLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.skyLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.skyLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        if (blockLight == null && !hasBlockLight) return 0;
        this.blockLight = getLightArray();
        int l = blockLight[(y << 7) | (z << 3) | (x >> 1)] & 0xff;
        if ((x & 1) == 0) {
            return l & 0x0f;
        }
        return l >> 4;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        if (this.blockLight == null) {
            if (hasBlockLight) {
                this.blockLight = getLightArray();
            } else if (level == 0) {
                return;
            } else {
                this.blockLight = new byte[2048];
            }
        }
        int i = (y << 7) | (z << 3) | (x >> 1);
        int old = this.blockLight[i] & 0xff;
        if ((x & 1) == 0) {
            this.blockLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.blockLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
    }

    @Override
    public byte[] getSkyLightArray() {
        if (this.skyLight != null) return skyLight;
        if (hasSkyLight) {
            if (compressedLight != null) {
                inflate();
                return this.skyLight;
            }
            return EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
        } else {
            return EmptyChunkSection.EMPTY_LIGHT_ARR;
        }
    }

    private void inflate() {
        try {
            if (compressedLight != null && compressedLight.length != 0) {
                byte[] inflated = Zlib.inflate(compressedLight);
                blockLight = Arrays.copyOfRange(inflated, 0, 2048);
                if (inflated.length > 2048) {
                    skyLight = Arrays.copyOfRange(inflated, 2048, 4096);
                } else {
                    skyLight = new byte[2048];
                    if (hasSkyLight) {
                        Arrays.fill(skyLight, (byte) 0xFF);
                    }
                }
                compressedLight = null;
            } else {
                blockLight = new byte[2048];
                skyLight = new byte[2048];
                if (hasSkyLight) {
                    Arrays.fill(skyLight, (byte) 0xFF);
                }
            }
        } catch (IOException e) {
            log.error("Failed to decompress a chunk section", e);
        }
    }

    @Override
    public byte[] getLightArray() {
        if (this.blockLight != null) return blockLight;
        if (hasBlockLight) {
            inflate();
            return this.blockLight;
        } else {
            return EmptyChunkSection.EMPTY_LIGHT_ARR;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private byte[] toXZY(char[] raw) {
        byte[] buffer = ThreadCache.byteCache6144.get();
        for (int i = 0; i < 4096; i++) {
            buffer[i] = (byte) (raw[i] >> 4);
        }
        for (int i = 0, j = 4096; i < 4096; i += 2, j++) {
            buffer[j] = (byte) (((raw[i + 1] & 0xF) << 4) | (raw[i] & 0xF));
        }
        return buffer;
    }

    @Override
    public synchronized void writeTo(@Nonnull BinaryStream stream) {
        layerStorage.writeTo(stream);
    }

    @SuppressWarnings("java:S1905")
    @Nullable
    private List<byte[]> saveData(
            BlockStorage storage, byte[] idsBase, @Nullable byte[] idsExtra, 
            NibbleArray dataBase, @Nullable NibbleArray dataExtra) {
        boolean huge = storage.hasBlockDataHuge();
        boolean big = huge || storage.hasBlockDataBig();
        List<byte[]> hugeList = big? new ArrayList<>(huge? 3 : 1) : null;
        if (big) {
            hugeList.add(new byte[BlockStorage.SECTION_SIZE]);
        }
        
        storage.iterateStates(((bx, by, bz, state) -> {
            int anvil = getAnvilIndex(bx, by, bz);
            int blockId = state.getBlockId();
            if (blockId == 0) {
                return;
            }
            
            idsBase[anvil] = (byte)(blockId & 0xFF);
            if (idsExtra != null) {
                idsExtra[anvil] = (byte)(blockId >>> 8 & 0xFF);
            }
            
            @SuppressWarnings("deprecation")
            int unsignedIntData = state.getBigDamage();
            dataBase.set(anvil, (byte)(unsignedIntData & 0x0F));
            if (dataExtra != null) {
                dataExtra.set(anvil, (byte)(unsignedIntData >>> 4 & 0x0F));
            }
            
            if (!big) {
                return;
            }

            hugeList.get(0)[anvil] = (byte)(unsignedIntData >>> 8 & 0xFF);
            if (huge) {
                saveHugeData(hugeList, state, anvil, unsignedIntData);
            }
        }));
        
        return hugeList;
    }

    private void saveHugeData(List<byte[]> hugeList, BlockState state, int anvil, int intData) {
        int bitSize = state.getBitSize();
        if (bitSize <= 16) {
            return;
        }
        intData >>>= 16;
        int processedBits = 16;
        
        int pos = 1;
        for (; processedBits < 32 && processedBits <= bitSize; processedBits += 8, pos++, intData >>>= 8) {
            byte[] blob = allocateBlob(hugeList, pos);
            blob[anvil] = (byte)(intData & 0xFF);
        }
        
        if (processedBits >= bitSize) {
            return;
        }

        BigInteger hugeData = state.getHugeDamage().shiftRight(32);
        for (; processedBits <= bitSize; processedBits += 8, pos++, hugeData = hugeData.shiftRight(8)) {
            byte[] blob = allocateBlob(hugeList, pos);
            blob[anvil] = hugeData.and(BYTE_MASK).byteValue(); 
        }
    }

    private byte[] allocateBlob(List<byte[]> hugeList, int pos) {
        byte[] blob;
        if (hugeList.size() <= pos) {
            blob = new byte[BlockStorage.SECTION_SIZE];
            hugeList.add(blob);
        } else {
            blob = hugeList.get(pos);
        }
        return blob;
    }

    @Nonnull
    @Override
    public synchronized CompoundTag toNBT() {
        CompoundTag s = new CompoundTag();
        compressStorageLayers();
        // For simplicity, not using the actual palette format to save in the disk
        // And for better compatibility, attempting to use the closest to the old format as possible
        // Version 0 = old format (single block storage, Blocks and Data tags only)
        // Version 1 = old format extended same as 0 but may have BlocksExtra and DataExtra
        // Version 7 = new format (multiple block storage, may have Blocks, BlocksExtra, Data and DataExtra)
        // Version 8 = not the same as network version 8 because it's not pallet, it's like 7 but everything is filled even when an entire section is empty
        s.putByte("Y", (getY()));
        int version = SAVE_STORAGE_VERSION;
        ListTag<CompoundTag> storageList = new ListTag<>(STORAGE_TAG_NAME);
        int blockStorages = Math.max(1, layerStorage.size());
        for (int layer = 0; layer < blockStorages; layer++) {
            BlockStorage storage = layerStorage.getStorageOrEmpty(layer);
            
            CompoundTag storageTag;
            if (layer == 0 && blockStorages == 1) {
                storageTag = s;
                if (!storage.hasBlockDataExtras() && !storage.hasBlockIdExtras()) {
                    version = 0;
                } else {
                    version = 1;
                }
            } else {
                storageTag = new CompoundTag();
            }
            
            if (version == 0 || storage.hasBlockIds()) {
                byte[] idsBase = new byte[BlockStorage.SECTION_SIZE];
                byte[] idsExtra = storage.hasBlockIdExtras()? new byte[BlockStorage.SECTION_SIZE] : null;
                        
                NibbleArray dataBase = new NibbleArray(BlockStorage.SECTION_SIZE);
                NibbleArray dataExtra = storage.hasBlockDataExtras()? new NibbleArray(BlockStorage.SECTION_SIZE) : null;
                List<byte[]> dataHuge = saveData(storage, idsBase, idsExtra, dataBase, dataExtra);

                storageTag.putByteArray("Blocks", idsBase);
                storageTag.putByteArray("Data", dataBase.getData());

                if (idsExtra != null) {
                    storageTag.putByteArray("BlocksExtra", idsExtra);
                }
                
                if (dataExtra != null) {
                    storageTag.putByteArray("DataExtra", dataExtra.getData());
                }
                
                if (dataHuge != null) {
                    ListTag<ByteArrayTag> hugeDataListTag = new ListTag<>(HUGE_TAG_NAME);
                    for (byte[] hugeData : dataHuge) {
                        hugeDataListTag.add(new ByteArrayTag("", hugeData));
                    }
                    storageTag.putList(hugeDataListTag);
                }
            }
            
            if (version >= SAVE_STORAGE_VERSION) {
                storageList.add(storageTag);
            }
        }
        s.putByte("Version", version);
        s.putByte("ContentVersion", getContentVersion());
        if (version >= SAVE_STORAGE_VERSION) {
            s.putList(storageList);
        }
        s.putByteArray("BlockLight", getLightArray());
        s.putByteArray("SkyLight", getSkyLightArray());
        return s;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public synchronized void compressStorageLayers() {
        layerStorage.compress(this::setLayerStorage);
    }

    public boolean compress() {
        if (blockLight != null) {
            byte[] arr1 = blockLight;
            hasBlockLight = !Utils.isByteArrayEmpty(arr1);
            byte[] arr2;
            if (skyLight != null) {
                arr2 = skyLight;
                hasSkyLight = !Utils.isByteArrayEmpty(arr2);
            } else if (hasSkyLight) {
                arr2 = EmptyChunkSection.EMPTY_SKY_LIGHT_ARR;
            } else {
                arr2 = EmptyChunkSection.EMPTY_LIGHT_ARR;
                hasSkyLight = false;
            }
            blockLight = null;
            skyLight = null;
            byte[] toDeflate = null;
            if (hasBlockLight && hasSkyLight && arr2 != EmptyChunkSection.EMPTY_SKY_LIGHT_ARR) {
                toDeflate = Binary.appendBytes(arr1, arr2);
            } else if (hasBlockLight) {
                toDeflate = arr1;
            }
            if (toDeflate != null) {
                try {
                    compressedLight = Zlib.deflate(toDeflate, 1);
                } catch (Exception e) {
                    log.error("Error compressing the light data", e);
                }
            }
            return true;
        }
        return false;
    }
    
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public synchronized List<Block> scanBlocks(LevelProvider provider, int offsetX, int offsetZ, BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        BlockStorage storage = layerStorage.getStorageOrNull(0);
        if (storage == null) {
            return Collections.emptyList();
        }
        
        List<Block> results = new ArrayList<>();
        BlockVector3 current = new BlockVector3();

        int offsetY = getY() << 4;
        int minX = Math.max(0, min.x - offsetX);
        int minY = Math.max(0, min.y - offsetY);
        int minZ = Math.max(0, min.z - offsetZ);
        
        for (int x = Math.min(max.x - offsetX, 15); x >= minX; x--) {
            current.x = offsetX + x;
            for (int z = Math.min(max.z - offsetZ, 15); z >= minZ; z--) {
                current.z = offsetZ + z;
                for (int y = Math.min(max.y - offsetY, 15); y >= minY; y--) {
                    current.y = offsetY + y;
                    BlockState state = storage.getBlockState(x, y, z);
                    if (condition.test(current, state)) {
                        results.add(state.getBlockRepairing(provider.getLevel(), current, 0));
                    }
                }
            }
        }

        return results;
    }

    @SneakyThrows(CloneNotSupportedException.class)
    @Nonnull
    public ChunkSection copy() {
        return new ChunkSection(
                this.y,
                this.layerStorage.clone(),
                this.blockLight == null ? null : this.blockLight.clone(),
                this.skyLight == null ? null : this.skyLight.clone(),
                this.compressedLight == null ? null : this.compressedLight.clone(),
                this.hasBlockLight,
                this.hasSkyLight
        );
    }
    
    @Override
    public int getMaximumLayer() {
        return 1;
    }

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    @Override
    public int getContentVersion() {
        return contentVersion;
    }

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.1.0-PN")
    @Override
    public void setContentVersion(int contentVersion) {
        this.contentVersion = contentVersion;
    }

    @Override
    public boolean hasBlocks() {
        return layerStorage.hasBlocks();
    }

}
