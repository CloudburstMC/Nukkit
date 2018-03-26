package cn.nukkit.server.level.chunk;

import cn.nukkit.server.level.util.NibbleArray;
import com.google.common.base.Preconditions;

public class ChunkSection {

    private static final int SECTION_SIZE = 4096;
    private final byte[] ids;
    private final NibbleArray data;
    private final NibbleArray skyLight;
    private final NibbleArray blockLight;

    public ChunkSection() {
        this.ids = new byte[SECTION_SIZE];
        this.data = new NibbleArray(SECTION_SIZE);
        this.skyLight = new NibbleArray(SECTION_SIZE);
        this.blockLight = new NibbleArray(SECTION_SIZE);
    }

    public ChunkSection(byte[] ids, byte[] data, byte[] skyLight, byte[] blockLight) {
        Preconditions.checkArgument(ids.length == SECTION_SIZE);
        Preconditions.checkArgument(data.length == SECTION_SIZE / 2);
        Preconditions.checkArgument(skyLight.length == SECTION_SIZE / 2);
        Preconditions.checkArgument(blockLight.length == SECTION_SIZE / 2);
        this.ids = ids;
        this.data = new NibbleArray(data);
        this.skyLight = new NibbleArray(skyLight);
        this.blockLight = new NibbleArray(blockLight);
    }

    public ChunkSection(byte[] ids, NibbleArray data, NibbleArray skyLight, NibbleArray blockLight) {
        this.ids = ids;
        this.data = data;
        this.skyLight = skyLight;
        this.blockLight = blockLight;
    }

    public int getBlockId(int x, int y, int z) {
        checkBounds(x, y, z);
        return ids[blockPosition(x, y, z)];
    }

    public byte getBlockData(int x, int y, int z) {
        checkBounds(x, y, z);
        return data.get(blockPosition(x, y, z));
    }

    public byte getSkyLight(int x, int y, int z) {
        checkBounds(x, y, z);
        return skyLight.get(blockPosition(x, y, z));
    }

    public byte getBlockLight(int x, int y, int z) {
        checkBounds(x, y, z);
        return blockLight.get(blockPosition(x, y, z));
    }

    public void setBlockId(int x, int y, int z, byte id) {
        checkBounds(x, y, z);
        ids[blockPosition(x, y, z)] = id;
    }

    public void setBlockData(int x, int y, int z, byte data) {
        checkBounds(x, y, z);
        this.data.set(blockPosition(x, y, z), data);
    }

    public void setSkyLight(int x, int y, int z, byte val) {
        checkBounds(x, y, z);
        skyLight.set(blockPosition(x, y, z), val);
    }

    public void setBlockLight(int x, int y, int z, byte val) {
        checkBounds(x, y, z);
        blockLight.set(blockPosition(x, y, z), val);
    }

    public byte[] getIds() {
        return ids;
    }

    public NibbleArray getData() {
        return data;
    }

    public NibbleArray getSkyLight() {
        return skyLight;
    }

    public NibbleArray getBlockLight() {
        return blockLight;
    }

    public ChunkSection copy() {
        return new ChunkSection(
                ids.clone(),
                data.copy(),
                skyLight.copy(),
                blockLight.copy()
        );
    }

    public boolean isEmpty() {
        for (byte id : ids) {
            if (id != 0) {
                return false;
            }
        }
        return true;
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
