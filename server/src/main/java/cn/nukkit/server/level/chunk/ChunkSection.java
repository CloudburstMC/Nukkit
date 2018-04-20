package cn.nukkit.server.level.chunk;

import cn.nukkit.server.level.util.NibbleArray;
import cn.nukkit.server.level.util.Palette;
import cn.nukkit.server.nbt.util.VarInt;
import com.google.common.base.Preconditions;
import gnu.trove.list.array.TIntArrayList;
import io.netty.buffer.ByteBuf;

public class ChunkSection {
    private static final int SECTION_SIZE = 4096;
    private static final byte CHUNKSECTION_VERSION = 8;
    private final TIntArrayList runtimeIds = new TIntArrayList(16, -1);
    private final short[][] indexes;
    private final NibbleArray skyLight;
    private final NibbleArray blockLight;

    public ChunkSection() {
        this.indexes = new short[2][SECTION_SIZE];
        this.skyLight = new NibbleArray(SECTION_SIZE);
        this.blockLight = new NibbleArray(SECTION_SIZE);
        this.runtimeIds.add(0); // Air
    }

    public ChunkSection(short[][] indexes, int[] runtimeIds, byte[] skyLight, byte[] blockLight) {
        Preconditions.checkArgument(indexes.length == 2 && indexes[0].length == SECTION_SIZE && indexes[1].length == SECTION_SIZE);
        Preconditions.checkArgument(skyLight.length == SECTION_SIZE / 2);
        Preconditions.checkArgument(blockLight.length == SECTION_SIZE / 2);
        this.indexes = indexes;
        this.runtimeIds.addAll(runtimeIds);
        this.skyLight = new NibbleArray(skyLight);
        this.blockLight = new NibbleArray(blockLight);
    }

    private ChunkSection(short[][] indexes, int[] runtimeIds, NibbleArray skyLight, NibbleArray blockLight) {
        this.indexes = indexes;
        this.runtimeIds.addAll(runtimeIds);
        this.skyLight = skyLight;
        this.blockLight = blockLight;
    }

    public int getBlockId(int x, int y, int z, int layer) {
        checkBounds(x, y, z);
        return runtimeIds.get(indexes[layer][blockPosition(x, y, z)]);
    }

    public byte getSkyLight(int x, int y, int z) {
        checkBounds(x, y, z);
        return skyLight.get(blockPosition(x, y, z));
    }

    public byte getBlockLight(int x, int y, int z) {
        checkBounds(x, y, z);
        return blockLight.get(blockPosition(x, y, z));
    }

    public void setBlockId(int x, int y, int z, int layer, int runtimeId) {
        checkBounds(x, y, z);
        int blockPosition = blockPosition(x, y, z);
        short oldIndex = indexes[layer][blockPosition];
        int index;
        if ((index = runtimeIds.indexOf(runtimeId)) == -1) {
            runtimeIds.add(runtimeId);
            index = runtimeIds.lastIndexOf(runtimeId);
        }
        indexes[layer][blockPosition] = (short) index;
        checkForUnused(layer, oldIndex);
    }

    public void setSkyLight(int x, int y, int z, byte val) {
        checkBounds(x, y, z);
        skyLight.set(blockPosition(x, y, z), val);
    }

    public void setBlockLight(int x, int y, int z, byte val) {
        checkBounds(x, y, z);
        blockLight.set(blockPosition(x, y, z), val);
    }

    int[] getRuntimeIds() {
        return runtimeIds.toArray();
    }

    short[][] getIndexes() {
        return indexes;
    }

    public NibbleArray getSkyLight() {
        return skyLight;
    }

    public NibbleArray getBlockLight() {
        return blockLight;
    }

    void writeTo(ByteBuf buf){
        buf.writeByte(CHUNKSECTION_VERSION);
        buf.writeByte(indexes.length);
        for (short[] index : indexes) {
            int[] runtimeIds = this.runtimeIds.toArray();
            int blocksPerWord = Palette.getBlocksPerWord(runtimeIds.length);
            Palette palette = new Palette(blocksPerWord, false);
            int paletteInfo = Palette.getPalletInfo(palette.getVersion(), true);
            buf.writeByte(paletteInfo);
            palette.writeIndexes(buf, index);

            VarInt.writeSignedInt(buf, runtimeIds.length);
            for (int runtimeId : runtimeIds) {
                VarInt.writeSignedInt(buf, runtimeId);
            }
        }
    }

    public ChunkSection copy() {
        return new ChunkSection(
                indexes.clone(),
                runtimeIds.toArray(),
                skyLight.copy(),
                blockLight.copy()
        );
    }

    public boolean isEmpty() {
        if (runtimeIds.size() == 1) {
            return true;
        }
        return false;
    }

    private void checkForUnused(int layer, short index) {
        for (int i = 0; i < SECTION_SIZE; i++) {
            if (indexes[layer][i] == index) {
                return;
            }
        }
        runtimeIds.remove(index);
        for (int i = 0; i < indexes.length; i++) {
            short value = indexes[layer][i];
            if (value <= index) {
                continue;
            }

            indexes[layer][i] = --value;
        }
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
