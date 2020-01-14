package cn.nukkit.level.format.anvil;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.*;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    public static final int STORAGE_VERSION = 8;

    private final int y;

    // Array but with fixed size of 2 for now
    private final BlockStorage[] storage;

    protected byte[] blockLight;
    protected byte[] skyLight;
    protected byte[] compressedLight;
    protected boolean hasBlockLight;
    protected boolean hasSkyLight;

    private ChunkSection(int y, BlockStorage[] storage, byte[] blockLight, byte[] skyLight, byte[] compressedLight,
                         boolean hasBlockLight, boolean hasSkyLight) {
        this.y = y;
        this.storage = storage;
        this.skyLight = skyLight;
        this.compressedLight = compressedLight;
        this.hasBlockLight = hasBlockLight;
        this.hasSkyLight = hasSkyLight;
    }

    public ChunkSection(int y) {
        this.y = y;

        hasBlockLight = false;
        hasSkyLight = false;

        storage = new BlockStorage[]{ new BlockStorage(), new BlockStorage() };
    }

    public ChunkSection(CompoundTag nbt) {
        this.y = nbt.getByte("Y");

        storage = new BlockStorage[]{ new BlockStorage(), new BlockStorage() };

        int version = nbt.getByte("Version");

        ListTag<CompoundTag> storageList;
        if (version == 8) {
            storageList = nbt.getList("Storage", CompoundTag.class);
        } else if (version == 0) {
            storageList = new ListTag<>("Storage");
            storageList.add(nbt);
        } else {
            throw new ChunkException("Unsupported chunk section version: " + version);
        }

        for (int i = 0; i < storageList.size(); i++) {
            CompoundTag storageTag = storageList.get(i);
            BlockStorage storage = this.storage[i];

            byte[] blocks = storageTag.getByteArray("Blocks");
            byte[] blocksExtra = storageTag.getByteArray("BlocksExtra");
            if (blocksExtra.length == 0) {
                blocksExtra = new byte[blocks.length];
            }
            byte[] dataBytes = storageTag.getByteArray("Data");
            NibbleArray data = new NibbleArray(dataBytes);
            byte[] dataExtraBytes = storageTag.getByteArray("DataExtra");
            if (dataExtraBytes.length == 0) {
                dataExtraBytes = new byte[dataBytes.length];
            }
            NibbleArray dataExtra = new NibbleArray(dataExtraBytes);

            // Convert YZX to XZY
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        storage.setBlockId(x, y, z, blocks[index] & 0xFF | ((blocksExtra[index] & 0xFF) << 8));
                        storage.setBlockData(x, y, z, data.get(index) & 0xF | ((dataExtra.get(index) & 0xF) << 4));
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
        synchronized (storage) {
            return storage[0].getBlockId(x, y, z);
        }
    }

    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        synchronized (storage) {
            return storage[layer].getBlockId(x, y, z);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        synchronized (storage) {
            storage[0].setBlockId(x, y, z, id);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, int id) {
        synchronized (storage) {
            storage[layer].setBlockId(x, y, z, id);
        }
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        synchronized (storage) {
            storage[0].setFullBlock(x, y, z, (char) fullId);
        }
        return true;
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        synchronized (storage) {
            storage[layer].setFullBlock(x, y, z, (char) fullId);
        }
        return true;
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        synchronized (storage) {
            return storage[0].getBlockData(x, y, z);
        }
    }

    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        synchronized (storage) {
            return storage[layer].getBlockData(x, y, z);
        }
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        synchronized (storage) {
            storage[0].setBlockData(x, y, z, data);
        }
    }

    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        synchronized (storage) {
            storage[layer].setBlockData(x, y, z, data);
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        synchronized (storage) {
            return storage[0].getFullBlock(x, y, z);
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        synchronized (storage) {
            return storage[layer].getFullBlock(x, y, z);
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        synchronized (storage) {
            return setBlock(x, y, z, blockId, 0);
        }
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        synchronized (storage) {
            return setBlockAtLayer(x, y, z, layer, blockId, 0);
        }
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        synchronized (storage) {
            int fullId = storage[0].getAndSetFullBlock(x, y, z, block.getFullId());
            return Block.fullList[fullId].clone();
        }
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        synchronized (storage) {
            int fullId = storage[layer].getAndSetFullBlock(x, y, z, block.getFullId());
            return Block.fullList[fullId].clone();
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        int newFullId = (blockId << Block.DATA_BITS) + meta;
        synchronized (storage) {
            int previousFullId = storage[0].getAndSetFullBlock(x, y, z, newFullId);
            return (newFullId != previousFullId);
        }
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        int newFullId = (blockId << Block.DATA_BITS) + meta;
        synchronized (storage) {
            int previousFullId = storage[layer].getAndSetFullBlock(x, y, z, newFullId);
            return (newFullId != previousFullId);
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
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil[index] = (byte) ((storage[layer].getBlockId(x, y, z) >> 8) & 0xFF);
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
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil[index] = (byte) (storage[layer].getBlockId(x, y, z) & 0xFF);
                    }
                }
            }
            return anvil;
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
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil.set(index, (byte) (storage[layer].getBlockData(x, y, z) & 0xF));
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
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil.set(index, (byte) ((storage[layer].getBlockData(x, y, z) >> 4) & 0xF));
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
            stream.putByte((byte) STORAGE_VERSION);
            stream.putByte((byte) storage.length);
            for (BlockStorage blockStorage : storage) {
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
        // For simplicity, not using the actual palette format to save in the disk
        CompoundTag s = new CompoundTag();
        s.putByte("Y", (getY()));
        s.putByte("Version", STORAGE_VERSION);
        ListTag<CompoundTag> storageList = new ListTag<>("Storage");
        synchronized (storage) {
            for (int layer = 0; layer < storage.length; layer++) {
                CompoundTag storageTag = new CompoundTag();
                storageTag.putByteArray("Blocks", getIdArray(layer));
                storageTag.putByteArray("BlocksExtra", getIdExtraArray(layer));
                storageTag.putByteArray("Data", getDataArray(layer));
                storageTag.putByteArray("DataExtra", getDataExtraArray(layer));
                storageList.add(storageTag);
            }
        }
        s.putList(storageList);
        s.putByteArray("BlockLight", getLightArray());
        s.putByteArray("SkyLight", getSkyLightArray());
        return s;
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

    public ChunkSection copy() {
        BlockStorage[] storageCopy = new BlockStorage[this.storage.length];
        for (int i = 0; i < storageCopy.length; i++) {
            storageCopy[i] = this.storage[i].copy();
        }
        return new ChunkSection(
                this.y,
                storageCopy,
                this.blockLight == null ? null : this.blockLight.clone(),
                this.skyLight == null ? null : this.skyLight.clone(),
                this.compressedLight == null ? null : this.compressedLight.clone(),
                this.hasBlockLight,
                this.hasSkyLight
        );
    }
}
