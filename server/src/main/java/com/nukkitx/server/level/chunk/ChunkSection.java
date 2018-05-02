package com.nukkitx.server.level.chunk;

import com.google.common.base.Preconditions;
import com.nukkitx.server.level.util.BlockStorage;
import com.nukkitx.server.level.util.NibbleArray;
import io.netty.buffer.ByteBuf;

public class ChunkSection {
    private static final int SECTION_SIZE = 4096;
    private static final byte CHUNKSECTION_VERSION = 8;
    private final BlockStorage[] storage;
    private final NibbleArray skyLight;
    private final NibbleArray blockLight;

    public ChunkSection() {
        this.storage = new BlockStorage[]{new BlockStorage(), new BlockStorage()};
        this.skyLight = new NibbleArray(SECTION_SIZE);
        this.blockLight = new NibbleArray(SECTION_SIZE);
    }

    public ChunkSection(BlockStorage[] storage, byte[] skyLight, byte[] blockLight) {
        Preconditions.checkArgument(storage.length >= 2);
        Preconditions.checkArgument(skyLight.length == SECTION_SIZE / 2);
        Preconditions.checkArgument(blockLight.length == SECTION_SIZE / 2);
        this.storage = storage;
        this.skyLight = new NibbleArray(skyLight);
        this.blockLight = new NibbleArray(blockLight);
    }

    private ChunkSection(BlockStorage[] storage, NibbleArray skyLight, NibbleArray blockLight) {
        this.storage = storage;
        this.skyLight = skyLight;
        this.blockLight = blockLight;
    }

    public int getBlockId(int x, int y, int z, int layer) {
        checkBounds(x, y, z);
        checkLayer(layer);
        return storage[layer].getBlockId(blockPosition(x, y, z));
    }

    public byte getSkyLight(int x, int y, int z) {
        checkBounds(x, y, z);
        return skyLight.get(blockPosition(x, y, z));
    }

    public byte getBlockLight(int x, int y, int z) {
        checkBounds(x, y, z);
        return blockLight.get(blockPosition(x, y, z));
    }

    public void setBlockId(int x, int y, int z, int layer, int id) {
        checkBounds(x, y, z);
        checkLayer(layer);
        storage[layer].setBlockId(blockPosition(x, y, z), id);
    }

    public void setSkyLight(int x, int y, int z, byte val) {
        checkBounds(x, y, z);
        skyLight.set(blockPosition(x, y, z), val);
    }

    public void setBlockLight(int x, int y, int z, byte val) {
        checkBounds(x, y, z);
        blockLight.set(blockPosition(x, y, z), val);
    }

    public NibbleArray getSkyLight() {
        return skyLight;
    }

    public NibbleArray getBlockLight() {
        return blockLight;
    }

    void writeTo(ByteBuf buf){
        buf.writeByte(CHUNKSECTION_VERSION);
        buf.writeByte(storage.length);
        for (BlockStorage blockStorage : storage) {
            blockStorage.writeTo(buf);
        }
    }

    public ChunkSection copy() {
        BlockStorage[] storage = new BlockStorage[this.storage.length];
        for (int i = 0; i < storage.length; i++) {
            storage[i] = this.storage[i].copy();
        }
        return new ChunkSection(storage, skyLight.copy(), blockLight.copy());
    }

    public boolean isEmpty() {
        for (BlockStorage blockStorage : storage) {
            if (!blockStorage.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void checkLayer(int layer) {
        Preconditions.checkArgument(layer >= 0 && layer <= storage.length);
    }

    private static int blockPosition(int x, int y, int z) {
        return (x << 8) + (z << 4) + y;
    }

    private static void checkBounds(int x, int y, int z) {
        Preconditions.checkArgument(x >= 0 && x < 16, "x (%s) is not between 0 and 15", x);
        Preconditions.checkArgument(y >= 0 && y < 16, "y (%s) is not between 0 and 15", y);
        Preconditions.checkArgument(z >= 0 && z < 16, "z (%s) is not between 0 and 15", z);
    }
}
