package cn.nukkit.level.format.anvil;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.anvil.palette.Palette;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.math.MathHelper;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ThreadCache;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.Zlib;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.IntConsumer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkSection implements cn.nukkit.level.format.ChunkSection {

    private static final byte VERSION = 8;

    private static final byte[] EMPTY_MATRIX = EmptyChunkSection.EMPTY_DATA_ARR;
    static {
        Arrays.fill(EMPTY_MATRIX, (byte) 0);
    }

    private final int y;

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

        storage = new BlockStorage[]{new BlockStorage(), null};
    }

    public ChunkSection(CompoundTag nbt) {
        this.y = nbt.getByte("Y");
        storage = new BlockStorage[2];

        byte[] blocks = nbt.getByteArray("Blocks");
        NibbleArray data = new NibbleArray(nbt.getByteArray("Data"));
        NibbleArray matrix = new NibbleArray(nbt.getByteArray("NukkitMatrix"));
        if (matrix.getData().length <= 0) {
            matrix = new NibbleArray(EMPTY_MATRIX);
        }
        storage[0] = new BlockStorage();

        byte[] blocks2 = nbt.getByteArray("Blocks2");
        NibbleArray data2 = null;
        NibbleArray matrix2 = null;
        boolean hasLayer2 = blocks2.length > 0;
        if (hasLayer2) {
            data2 = new NibbleArray(nbt.getByteArray("Data2"));
            matrix2 = new NibbleArray(nbt.getByteArray("NukkitMatrix2"));
            if (matrix2.getData().length <= 0) {
                matrix2 = new NibbleArray(EMPTY_MATRIX);
            }
            storage[1] = new BlockStorage();
        }

        // Convert YZX to XZY
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    int index = getAnvilIndex(x, y, z);
                    storage[0].setBlockId(x, y, z, (blocks[index] & 0xff) + 256 * (matrix.get(index) & 1));
                    storage[0].setBlockData(x, y, z, data.get(index));
                    if (hasLayer2) {
                        storage[1].setBlockId(x, y, z, (blocks2[index] & 0xff) + 256 * (matrix2.get(index) & 1));
                        storage[1].setBlockData(x, y, z, data2.get(index));
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
        checkLayer(layer);
        synchronized (storage[layer]) {
            return storage[layer].getBlockId(x, y, z);
        }
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        setBlockId(x, y, z, id, 0);
    }

    @Override
    public void setBlockId(int x, int y, int z, int id, int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            storage[layer].setBlockId(x, y, z, id);
        }
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        return setFullBlockId(x, y, z, fullId, 0);
    }

    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId, int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            storage[layer].setFullBlock(x, y, z, (char) fullId);
        }
        return true;
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return getBlockData(x, y, z, 0);
    }

    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            return storage[layer].getBlockData(x, y, z);
        }
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        setBlockData(x, y, z, data, 0);
    }

    @Override
    public void setBlockData(int x, int y, int z, int data, int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            storage[layer].setBlockData(x, y, z, data);
        }
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(x, y, z, 0);
    }

    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            return storage[layer].getFullBlock(x, y, z);
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return setBlock(x, y, z, blockId, 0);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        return getAndSetBlock(x, y, z, block, 0);
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block, int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            int fullId = storage[layer].getAndSetFullBlock(x, y, z, block.getFullId());
            return Block.fullList[fullId].clone();
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        return setBlock(x, y, z, blockId, meta, 0);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta, int layer) {
        checkLayer(layer);
        int newFullId = (blockId << 4) + meta;
        synchronized (storage[layer]) {
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
    public byte[] getIdArray() {
        return getIdArray(0);
    }

    @Override
    public byte[] getIdArray(int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            byte[] anvil = new byte[4096];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil[index] = (byte) storage[layer].getBlockId(x, y, z);
                    }
                }
            }
            return anvil;
        }
    }

    @Override
    public byte[] getDataArray() {
        return getDataArray(0);
    }

    @Override
    public byte[] getDataArray(int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            NibbleArray anvil = new NibbleArray(4096);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil.set(index, (byte) storage[layer].getBlockData(x, y, z));
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
            return EmptyChunkSection.EMPTY_DATA_ARR;
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
            return EmptyChunkSection.EMPTY_DATA_ARR;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public byte[] getBytes() {
        synchronized (storage) {
            BinaryStream stream = new BinaryStream();
            stream.putByte(VERSION);

            int amount = hasLayer2() ? 2 : 1;
            stream.putByte((byte) amount);
            for (int layer = 0; layer < amount; layer++) {
                int[] indexIDs = new int[4096];
                LongList indexList = new LongArrayList();
                IntList runtimeIndex = new IntArrayList();

                int foundIndex = 0;
                int lastRuntimeID = -1;
                for (short blockIndex = 0; blockIndex < indexIDs.length; blockIndex++) {
                    int runtimeID = GlobalBlockPalette.getOrCreateRuntimeId(storage[layer].getFullBlock(blockIndex));
                    if (runtimeID != lastRuntimeID) {
                        foundIndex = indexList.indexOf(runtimeID);
                        if (foundIndex == -1) {
                            runtimeIndex.add(runtimeID);
                            indexList.add(runtimeID);
                            foundIndex = indexList.size() - 1;
                        }
                        lastRuntimeID = runtimeID;
                    }
                    indexIDs[blockIndex] = foundIndex;
                }

                // Get correct wordsize
                int value = indexList.size();
                int numberOfBits = MathHelper.fastFloor(MathHelper.log2(value) - 1) + 1;

                // Prepare palette
                int amountOfBlocks = MathHelper.fastFloor(32f / numberOfBits);
                Palette palette = new Palette(amountOfBlocks, false);

                byte paletteWord = (byte) ((byte) (palette.getVersion().getId() << 1 ) | 1);
                stream.putByte(paletteWord);
                palette.addIndexIDs(indexIDs);
                stream.put(palette.finish());

                // Write runtimeIDs
                stream.putVarInt(indexList.size());
                runtimeIndex.forEach((IntConsumer) stream::putVarInt);
            }

            return stream.getBuffer();
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
                arr2 = EmptyChunkSection.EMPTY_DATA_ARR;
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

    @Override
    public byte[] getMatrixArray() {
        return getMatrixArray(0);
    }

    @Override
    public byte[] getMatrixArray(int layer) {
        checkLayer(layer);
        synchronized (storage[layer]) {
            NibbleArray anvil = new NibbleArray(4096);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int index = getAnvilIndex(x, y, z);
                        anvil.set(index, (byte) storage[layer].getMatrixElement(x, y, z));
                    }
                }
            }
            return anvil.getData();
        }
    }

    @Override
    public ChunkSection copy() {
        return new ChunkSection(
                this.y,
                new BlockStorage[]{this.storage[0].copy(), this.storage[1] == null ? null : this.storage[1].copy()},
                this.blockLight == null ? null : this.blockLight.clone(),
                this.skyLight == null ? null : this.skyLight.clone(),
                this.compressedLight == null ? null : this.compressedLight.clone(),
                this.hasBlockLight,
                this.hasSkyLight
        );
    }

    @Override
    public boolean hasLayer2() {
        return storage[1] != null;
    }

    private void checkLayer(int layer) {
        Preconditions.checkArgument(layer >= 0 && layer <= 1, "Unknown block layer: " + layer);
        if (storage[layer] == null) {
            storage[layer] = new BlockStorage();
            synchronized (storage[layer]) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            storage[layer].setFullBlock(x, y, z, 0);
                        }
                    }
                }
            }
        }
    }
}
