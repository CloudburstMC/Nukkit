package cn.nukkit.level.format.anvil.util;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public class BlockStorage {
    private static final int SECTION_SIZE = 4096;
    private final byte[] blockIds;
    private final byte[] blockIdsExtra;
    private final NibbleArray blockData;

    public BlockStorage() {
        blockIds = new byte[SECTION_SIZE];
        blockIdsExtra = new byte[SECTION_SIZE];
        blockData = new NibbleArray(SECTION_SIZE);
    }

    private BlockStorage(byte[] blockIds, byte[] blockIdsExtra, NibbleArray blockData) {
        this.blockIds = blockIds;
        this.blockIdsExtra = blockIdsExtra;
        this.blockData = blockData;
    }

    private static int getIndex(int x, int y, int z) {
        int index = (x << 8) + (z << 4) + y; // XZY = Bedrock format
        Preconditions.checkArgument(index >= 0 && index < SECTION_SIZE, "Invalid index");
        return index;
    }

    public int getBlockData(int x, int y, int z) {
        return blockData.get(getIndex(x, y, z)) & 0xf;
    }

    public int getBlockId(int x, int y, int z) {
        int index = getIndex(x, y, z);
        return (blockIds[index] & 0xFF) | (blockIdsExtra[index] & 0xFF) << 8;
    }

    public void setBlockId(int x, int y, int z, int id) {
        int index = getIndex(x, y, z);
        blockIds[index] = (byte) (id & 0xff);
        blockIdsExtra[index] = (byte) ((id >> 8) & 0xff);
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
        Preconditions.checkArgument(value < 0x1fff, "Invalid full block");
        byte oldBlockExtra = blockIdsExtra[index];
        byte oldBlock = blockIds[index];
        byte oldData = blockData.get(index);
        byte newBlockExtra = (byte) ((value & 0xff000) >> 12);
        byte newBlock = (byte) ((value & 0xff0) >> 4);
        byte newData = (byte) (value & 0xf);
        if (oldBlock != newBlock) {
            blockIds[index] = newBlock;
        }
        if (oldBlockExtra != newBlockExtra) {
            blockIdsExtra[index] = newBlockExtra;
        }
        if (oldData != newData) {
            blockData.set(index, newData);
        }
        return (((oldBlockExtra & 0xff) << 12) | (oldBlock & 0xff) << 4) | oldData;
    }

    private int getFullBlock(int index) {
        byte block = blockIds[index];
        byte extra = blockIdsExtra[index];
        byte data = blockData.get(index);
        return (extra & 0xff) << 12 | (block & 0xff) << 4 | data;
    }

    private void setFullBlock(int index, int value) {
        Preconditions.checkArgument(value < 0xfff, "Invalid full block");
        byte extra = (byte) ((value & 0xff000) >> 12);
        byte block = (byte) ((value & 0xff0) >> 4);
        byte data = (byte) (value & 0xf);

        blockIds[index] = block;
        blockIdsExtra[index] = extra;
        blockData.set(index, data);
    }

    public byte[] getBlockIds() {
        return Arrays.copyOf(blockIds, blockIds.length);
    }

    public byte[] getBlockIdsExtra() {
        return Arrays.copyOf(blockIdsExtra, blockIdsExtra.length);
    }

    public byte[] getBlockData() {
        return blockData.getData();
    }

    public BlockStorage copy() {
        return new BlockStorage(blockIds.clone(), blockIdsExtra.clone(), blockData.copy());
    }
}
