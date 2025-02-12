package cn.nukkit.level.format.anvil.util;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.utils.BinaryStream;

public class BlockStorage {

    private static final int SECTION_SIZE = 4096;
    private final byte[] blockIds;
    private final NibbleArray blockData;

    public BlockStorage() {
        blockIds = new byte[SECTION_SIZE];
        blockData = new NibbleArray(SECTION_SIZE);
    }

    private BlockStorage(byte[] blockIds, NibbleArray blockData) {
        this.blockIds = blockIds;
        this.blockData = blockData;
    }

    private static int getIndex(int x, int y, int z) {
        int index = (x << 8) + (z << 4) + y; // XZY = Bedrock format
        if (index < 0 || index >= SECTION_SIZE) throw new IllegalArgumentException("Invalid index");
        return index;
    }

    public int getBlockData(int x, int y, int z) {
        return blockData.get(getIndex(x, y, z)) & 0xf;
    }

    public int getBlockId(int x, int y, int z) {
        return blockIds[getIndex(x, y, z)] & 0xFF;
    }

    public void setBlockId(int x, int y, int z, int id) {
        blockIds[getIndex(x, y, z)] = (byte) (id & 0xff);
    }

    public void setBlockData(int x, int y, int z, int data) {
        blockData.set(getIndex(x, y, z), (byte) data);
    }

    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(getIndex(x, y, z));
    }

    public void setFullBlock(int x, int y, int z, int value) {
        this.setFullBlock(getIndex(x, y, z), value);
    }

    public int getAndSetFullBlock(int x, int y, int z, int value) {
        return getAndSetFullBlock(getIndex(x, y, z), value);
    }

    private int getAndSetFullBlock(int index, int value) {
        // Convert to old data bits
        int block = value >> Block.DATA_BITS;
        int meta = value & Block.DATA_MASK;
        value = (block << 4) + Math.min(meta, 15);

        if (value >= 0x1fff) throw new IllegalArgumentException("Invalid full block " + value);
        int oldBlock = blockIds[index] & 0xff;
        byte oldData = blockData.get(index);
        byte newData = (byte) (value & 0xf);
        if (oldBlock != block) {
            blockIds[index] = (byte) (block & 0xff);
        }
        if (oldData != newData) {
            blockData.set(index, newData);
        }

        // Convert to new data bits
        return (oldBlock << Block.DATA_BITS) | oldData;
    }

    private int getFullBlock(int index) {
        int b = blockIds[index] & 0xff;

        // Convert to new data bits
        return (b << Block.DATA_BITS) | blockData.get(index);
    }

    private void setFullBlock(int index, int value) {
        // Convert to old data bits
        int block = value >> Block.DATA_BITS;
        int meta = value & Block.DATA_MASK;
        value = (block << 4) + Math.min(meta, 15);

        if (value >= 0x1fff) throw new IllegalArgumentException("Invalid full block " + value);
        blockIds[index] = (byte) (block & 0xff);
        blockData.set(index, (byte) (value & 0xf));
    }

    public void writeTo(int sectionY, BinaryStream stream) {
        stream.putByte((byte) 9); // SubChunk version

        PalettedBlockStorage layer = PalettedBlockStorage.createFromBlockPalette();
        for (int i = 0; i < SECTION_SIZE; i++) {
            layer.setBlock(i, GlobalBlockPalette.getOrCreateRuntimeId(blockIds[i] & 0xff, blockData.get(i)));
        }

        stream.putByte((byte) 1); // layers

        stream.putByte((byte) sectionY);

        layer.writeTo(stream);
    }

    public BlockStorage copy() {
        return new BlockStorage(blockIds.clone(), blockData.copy());
    }
}
