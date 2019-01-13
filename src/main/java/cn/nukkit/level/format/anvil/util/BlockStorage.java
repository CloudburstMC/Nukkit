package cn.nukkit.level.format.anvil.util;

import com.google.common.base.Preconditions;

import java.util.Arrays;

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
        Preconditions.checkArgument(index >= 0 && index < SECTION_SIZE, "Invalid index");
        return index;
    }

    public int getBlockData(int x, int y, int z) {
        return blockData.get(getIndex(x, y, z)) & 0xf;
    }

    public int getBlockId(int x, int y, int z) {
        int id = blockIds[getIndex(x, y, z)];
        return id < 0 ? 0xff - id : id;
    }

    public void setBlockId(int x, int y, int z, int id) {
        blockIds[getIndex(x, y, z)] = (byte) (id > 0xff ? 0xff - id : id);
    }

    public void setBlockData(int x, int y, int z, int data) {
        blockData.set(getIndex(x, y, z), (byte) data);
    }

    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(getIndex(x, y, z));
    }

    public void setFullBlock(int x, int y, int z, int value) {
        this.setFullBlock(getIndex(x, y, z), (short) value);
    }

    public int getAndSetFullBlock(int x, int y, int z, int value) {
        return getAndSetFullBlock(getIndex(x, y, z), (short) value);
    }

    private int getAndSetFullBlock(int index, short value) {
        Preconditions.checkArgument(value < 0x1fff, "Invalid full block");
        int oldBlock = blockIds[index];
        byte oldData = blockData.get(index);
        int newBlock = (value & 0x1ff0) >> 4;
        if (newBlock > 0xff) newBlock = 0xff - newBlock;
        byte newData = (byte) (value & 0xf);
        if (oldBlock != newBlock) {
            blockIds[index] = (byte) newBlock;
        }
        if (oldData != newData) {
            blockData.set(index, newData);
        }
        return ((oldBlock < 0 ? 0xff - oldBlock : oldBlock) << 4) | oldData;
    }

    private int getFullBlock(int index) {
        int block = blockIds[index];
        byte data = blockData.get(index);
        return ((block < 0 ? 0xff - block : block) << 4) | data;
    }

    private void setFullBlock(int index, short value) {
        Preconditions.checkArgument(value < 0x1fff, "Invalid full block");
        int block = (value & 0x1ff0) >> 4;
        if (block > 0xff) block = 0xff - block;
        byte data = (byte) (value & 0xf);

        blockIds[index] = (byte) block;
        blockData.set(index, data);
    }

    public byte[] getBlockIds() {
        return Arrays.copyOf(blockIds, blockIds.length);
    }

    public byte[] getBlockData() {
        return blockData.getData();
    }

    public BlockStorage copy() {
        return new BlockStorage(blockIds.clone(), blockData.copy());
    }
}
