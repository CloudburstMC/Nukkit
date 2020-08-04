package cn.nukkit.level.format.anvil;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.*;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    public static final int STREAM_STORAGE_VERSION = 8;
    public static final int SAVE_STORAGE_VERSION = 7;

    private final int y;

    private final List<BlockStorage> storage = new ArrayList<>(1);

    protected byte[] blockLight;
    protected byte[] skyLight;
    protected byte[] compressedLight;
    protected boolean hasBlockLight;
    protected boolean hasSkyLight;
    
    private int contentVersion;

    private ChunkSection(int y, List<BlockStorage> storage, byte[] blockLight, byte[] skyLight, byte[] compressedLight,
                         boolean hasBlockLight, boolean hasSkyLight) {
        this.y = y;
        this.storage.addAll(storage);
        this.skyLight = skyLight;
        this.compressedLight = compressedLight;
        this.hasBlockLight = hasBlockLight;
        this.hasSkyLight = hasSkyLight;
    }

    public ChunkSection(int y) {
        this.y = y;
        this.contentVersion = BaseChunk.CONTENT_VERSION;

        hasBlockLight = false;
        hasSkyLight = false;
        
        storage.add(new BlockStorage());
    }

    public ChunkSection(CompoundTag nbt) {
        this.y = nbt.getByte("Y");

        storage.add(new BlockStorage());
        
        contentVersion = nbt.getByte("ContentVersion");
        
        int version = nbt.getByte("Version");

        ListTag<CompoundTag> storageList;
        if (version == SAVE_STORAGE_VERSION || version == 8) {
            storageList = nbt.getList("Storage", CompoundTag.class);
        } else if (version == 0 || version == 1) {
            storageList = new ListTag<>("Storage");
            storageList.add(nbt);
        } else {
            throw new ChunkException("Unsupported chunk section version: " + version);
        }

        for (int i = 0; i < storageList.size(); i++) {
            CompoundTag storageTag = storageList.get(i);

            byte[] blocks = storageTag.getByteArray("Blocks");
            boolean hasBlockIds = false;
            if (blocks.length == 0) {
                blocks = new byte[BlockStorage.SECTION_SIZE];
            } else {
                hasBlockIds = true;
            }
            
            byte[] blocksExtra = storageTag.getByteArray("BlocksExtra");
            if (blocksExtra.length == 0) {
                blocksExtra = new byte[blocks.length];
            }
            
            byte[] dataBytes = storageTag.getByteArray("Data");
            if (dataBytes.length == 0) {
                dataBytes = new byte[BlockStorage.SECTION_SIZE / 2];
            } else {
                hasBlockIds = true;
            }
            NibbleArray data = new NibbleArray(dataBytes);
            
            byte[] dataExtraBytes = storageTag.getByteArray("DataExtra");
            if (dataExtraBytes.length == 0) {
                dataExtraBytes = new byte[dataBytes.length];
            }
            NibbleArray dataExtra = new NibbleArray(dataExtraBytes);
            
            ListTag<ByteArrayTag> hyperDataList = storageTag.getList("DataHyper", ByteArrayTag.class);
            int hyperDataSize = hyperDataList.size();
            
            if (hasBlockIds) {
                BlockStorage storage = getOrSetStorage(i);
    
                // Convert YZX to XZY
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            int index = getAnvilIndex(x, y, z);
                            storage.setBlockId(x, y, z, blocks[index] & 0xFF | ((blocksExtra[index] & 0xFF) << 8));
                            int fullData = data.get(index) & 0xF | ((dataExtra.get(index) & 0xF) << 4);
                            for (int dataIndex = 0; dataIndex < hyperDataSize; dataIndex++) {
                                int hyperData = (hyperDataList.get(dataIndex).data[index] & 0xFF) << 8 << (8 * dataIndex);
                                fullData |= hyperData;
                            }
                            storage.setBlockData(x, y, z, fullData);
                        }
                    }
                }
            }
        }

        this.blockLight = nbt.getByteArray("BlockLight");
        this.skyLight = nbt.getByteArray("SkyLight");
    }

    private static int getAnvilIndex(int x, int y, int z) {
        return (y << 8) + (z << 4) + x;
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
        synchronized (storage) {
            BlockStorage storage = getStorageIfExists(layer);
            return storage != null? storage.getBlockId(x, y, z) : 0;
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        setBlockId(x, y, z, 0, id);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, int id) {
        synchronized (storage) {
            getOrSetStorage(layer).setBlockId(x, y, z, id);
        }
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        setFullBlockId(x, y, z, 0, fullId);
        return true;
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        synchronized (storage) {
            getOrSetStorage(layer).setFullBlock(x, y, z, fullId);
        }
        return true;
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return getBlockData(x, y, z, 0);
    }

    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        synchronized (storage) {
            BlockStorage storage = getStorageIfExists(layer);
            return storage != null? storage.getBlockData(x, y, z) : 0;
        }
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        setBlockData(x, y, z, 0, data);
    }

    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        synchronized (storage) {
            getOrSetStorage(layer).setBlockData(x, y, z, data);
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(x, y, z, 0);
    }

    @Override
    public int[] getBlockState(int x, int y, int z, int layer) {
        synchronized (storage) {
            BlockStorage storage = getStorageIfExists(layer);
            return storage != null? storage.getBlockState(x, y, z) : new int[]{0,0};
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        synchronized (storage) {
            BlockStorage storage = getStorageIfExists(layer);
            return storage != null? storage.getFullBlock(x, y, z) : 0;
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return setBlockAtLayer(x, y, z, 0, blockId, 0);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        return setBlockAtLayer(x, y, z, layer, blockId, 0);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return getAndSetBlock(x, y, z, 0, block);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        synchronized (storage) {
            int[] before = getOrSetStorage(layer).getAndSetBlock(x, y, z, block.getId(), block.getDamage());
            return Block.get(before[0], before[1]);
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return setBlockAtLayer(x, y, z, 0, blockId, meta);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        synchronized (storage) {
            int[] previousState = getOrSetStorage(layer).getAndSetBlock(x, y, z, blockId, meta);
            return previousState[0] != blockId || previousState[1] != meta;
        }
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
    public byte[] getIdExtraArray(int layer) {
        synchronized (storage) {
            byte[] anvil = new byte[4096];
            BlockStorage storage = getStorageIfExists(layer);
            if (storage != null && storage.hasBlockIdExtras()) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            int index = getAnvilIndex(x, y, z);
                            anvil[index] = (byte) storage.getBlockIdExtra(x, y, z);
                        }
                    }
                }
            }
            return anvil;
        }
    }

    @Override
    public byte[] getIdArray(int layer) {
        synchronized (storage) {
            byte[] anvil = new byte[4096];
            BlockStorage storage = getStorageIfExists(layer);
            if (storage != null && storage.hasBlockIds()) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            int index = getAnvilIndex(x, y, z);
                            anvil[index] = (byte) storage.getBlockIdBase(x, y, z);
                        }
                    }
                }
            }
            return anvil;
        }
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    @Override
    public byte[][] getHyperDataArray(int layer) {
        synchronized (storage) {
            BlockStorage storage = getStorageIfExists(layer);
            if (storage == null) {
                return new byte[0][];
            }
            
            boolean hasA = storage.hasBlockDataHyperA();
            boolean hasB = storage.hasBlockDataHyperB();
            if (!hasA && !hasB) {
                return new byte[0][];
            }
            byte[][] hyperData = new byte[hasB? 3 : 1][4096];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        if (hasA) {
                            hyperData[0][index] = storage.getHyperDataA(x, y, z);
                        }
                        if (hasB) {
                            int hyperDataB = storage.getHyperDataB(x, y, z);
                            hyperData[1][index] = (byte) (hyperDataB & 0xFF);
                            hyperData[2][index] = (byte) (hyperDataB >>> 8 & 0xFF);
                        }
                    }
                }
            }
            
            return hyperData;
        }
    }

    @Override
    public byte[] getIdArray() {
        return getIdArray(0);
    }

    @Override
    public byte[] getDataArray() {
        return getDataArray(0);
    }

    @Override
    public byte[] getDataArray(int layer) {
        synchronized (storage) {
            NibbleArray anvil = new NibbleArray(4096);
            BlockStorage storage = getStorageIfExists(layer);
            if (storage != null && storage.hasBlockIds()) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            int index = getAnvilIndex(x, y, z);
                            anvil.set(index, (byte) storage.getBlockDataBase(x, y, z));
                        }
                    }
                }
            }
            return anvil.getData();
        }
    }
    
    @Override
    public byte[] getDataExtraArray(int layer) {
        synchronized (storage) {
            NibbleArray anvil = new NibbleArray(4096);
            BlockStorage storage = getStorageIfExists(layer);
            if (storage != null && storage.hasBlockDataExtras()) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            int index = getAnvilIndex(x, y, z);
                            anvil.set(index, (byte) storage.getBlockDataExtra(x, y, z));
                        }
                    }
                }
            }
            return anvil.getData();
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
            e.printStackTrace();
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

    public void writeToStream(BinaryStream stream) {
        synchronized (storage) {
            stream.putByte((byte) STREAM_STORAGE_VERSION);
            stream.putByte((byte) storage.size());
            for (BlockStorage blockStorage : storage) {
                if (blockStorage == null) {
                    blockStorage = new BlockStorage();
                }
                int[] ids = blockStorage.getBlockIdsExtended();
                int[] data = blockStorage.getBlockDataExtended();
                int[] blockStates = new int[ids.length];
                Int2IntOpenHashMap runtime2palette = new Int2IntOpenHashMap();
                ArrayList<Integer> palette2runtime = new ArrayList<>();
                AtomicInteger nextPaletteId = new AtomicInteger(0);
                //TODO Use compressed formats based on the value of this variable
                AtomicInteger maxRuntimeId = new AtomicInteger(0);
                for (int i = 0; i < blockStates.length; i++) {
                    int runtimeId = GlobalBlockPalette.getOrCreateRuntimeId(ids[i], data[i]);
                    int paletteId = runtime2palette.computeIfAbsent(runtimeId, rid -> {
                        int pid = nextPaletteId.getAndIncrement();
                        palette2runtime.add(rid);
                        if (maxRuntimeId.get() < rid) {
                            maxRuntimeId.set(rid);
                        }
                        return pid;
                    });
                    blockStates[i] = paletteId;
                }

                int bitsPerBlock = 16;
                stream.putByte( (byte) (1 | (bitsPerBlock << 1)) );
                for (int blockState : blockStates) {
                    stream.putLShort(blockState);
                }
                stream.putVarInt(palette2runtime.size());
                for (Integer runtimeId : palette2runtime) {
                    stream.putVarInt(runtimeId);
                }
            }
        }
    }

    @Override
    public byte[] getBytes() {
        BinaryStream stream = new BinaryStream();
        writeToStream(stream);
        return stream.getBuffer();
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag s = new CompoundTag();
        synchronized (storage) {
            compressStorageLayers();
            // For simplicity, not using the actual palette format to save in the disk
            // And for better compatibility, attempting to use the closest to the old format as possible
            // Version 0 = old format (single block storage, Blocks and Data tags only)
            // Version 1 = old format extended same as 0 but may have BlocksExtra and DataExtra
            // Version 7 = new format (multiple block storage, may have Blocks, BlocksExtra, Data and DataExtra)
            // Version 8 = not the same as network version 8 because it's not pallet, it's like 7 but everything is filled even when an entire section is empty
            s.putByte("Y", (getY()));
            int version = SAVE_STORAGE_VERSION;
            ListTag<CompoundTag> storageList = new ListTag<>("Storage");
            for (int layer = 0; layer < storage.size(); layer++) {
                BlockStorage storage = getStorageIfExists(layer);
                if (storage == null) {
                    storage = new BlockStorage();
                }
                
                CompoundTag storageTag;
                if (layer == 0 && this.storage.size() == 1) {
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
                    storageTag.putByteArray("Blocks", getIdArray(layer));
                    storageTag.putByteArray("Data", getDataArray(layer));
                    if (storage.hasBlockIdExtras()) {
                        storageTag.putByteArray("BlocksExtra", getIdExtraArray(layer));
                    }
                    if (storage.hasBlockDataExtras()) {
                        storageTag.putByteArray("DataExtra", getDataExtraArray(layer));
                    }
                    if (storage.hasBlockDataHyperA() || storage.hasBlockDataHyperB()) {
                        byte[][] hyperDataArray = getHyperDataArray(layer);
                        ListTag<ByteArrayTag> hyperDataListTag = new ListTag<>("DataHyper");
                        for (byte[] hyperData : hyperDataArray) {
                            hyperDataListTag.add(new ByteArrayTag("", hyperData));
                        }
                        storageTag.putList(hyperDataListTag);
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
        }
        s.putByteArray("BlockLight", getLightArray());
        s.putByteArray("SkyLight", getSkyLightArray());
        return s;
    }
    
    public void compressStorageLayers() {
        synchronized (storage) {
            // Remove unused storage layers
            for (int i = storage.size() - 1; i > 0; i--) {
                BlockStorage storage = this.storage.get(i);
                if (storage == null) {
                    this.storage.remove(i);
                } else if (storage.hasBlockIds()) {
                    storage.recheckBlocks();
                    if (storage.hasBlockIds()) {
                        break;
                    } else {
                        this.storage.remove(i);
                    }
                } else {
                    this.storage.remove(i);
                }
            }
        }
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
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }
    
    protected BlockStorage getOrSetStorage(int layer) {
        Preconditions.checkArgument(layer >= 0, "Negative storage layer");
        Preconditions.checkArgument(layer <= getMaximumLayer(), "Only layer 0 to %d are supported", getMaximumLayer());
        synchronized (storage) {
            BlockStorage blockStorage = layer < storage.size()? storage.get(layer) : null;
            if (blockStorage == null) {
                blockStorage = new BlockStorage();
                for (int i = storage.size(); i < layer; i++) {
                    storage.add(i, null);
                }
                if (layer == storage.size()) {
                    storage.add(layer, blockStorage);
                } else {
                    storage.set(layer, blockStorage);
                }
            }
    
            return blockStorage;
        }
    }
    
    protected BlockStorage getStorageIfExists(int layer) {
        Preconditions.checkArgument(layer >= 0, "Negative storage layer");
        if (layer > getMaximumLayer()) {
            return null;
        }
        synchronized (storage) {
            return layer < storage.size()? storage.get(layer) : null;
        }
    }

    public ChunkSection copy() {
        BlockStorage[] storageCopy = new BlockStorage[Math.min(this.storage.size(), getMaximumLayer() + 1)];
        for (int i = 0; i < storageCopy.length; i++) {
            BlockStorage blockStorage = this.getStorageIfExists(i);
            storageCopy[i] = blockStorage != null? blockStorage.copy() : null;
        }
        return new ChunkSection(
                this.y,
                Arrays.asList(storageCopy),
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
}
