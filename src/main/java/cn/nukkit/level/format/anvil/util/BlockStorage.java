package cn.nukkit.level.format.anvil.util;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public class BlockStorage {
    private static final int SECTION_SIZE = 4096;
    private final int[] blockIds;
    private final NibbleArray blockData;
    private boolean dirty = true;

    public BlockStorage() {
        blockIds = new int[SECTION_SIZE];
        blockData = new NibbleArray(SECTION_SIZE);
    }

    private BlockStorage(int[] blockIds, NibbleArray blockData) {
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
        return blockIds[getIndex(x, y, z)];
    }

    public void setBlockId(int x, int y, int z, int id) {
        int index = getIndex(x, y, z);
        int oldId = blockIds[index];
        if (oldId != id) {
            blockIds[index] = id;
            setDirty(true);
        }
    }

    public void setBlockData(int x, int y, int z, int data) {
        blockData.set(getIndex(x, y, z), (byte) data);
    }

    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(getIndex(x, y, z));
    }

    public void setFullBlock(int x, int y, int z, int value) {
        Preconditions.checkArgument(value < 0x1fff, "Invalid full block");
        int index = getIndex(x, y, z);
        int oldBlock = blockIds[index];
        int newBlock = (value & 0x1ff0) >> 4;
        if (oldBlock != newBlock) {
            blockIds[index] = newBlock;
            setDirty(true);
        }
        blockData.set(index, (byte) (value & 0xf));
    }

    public int getAndSetFullBlock(int x, int y, int z, int value) {
        Preconditions.checkArgument(value < 0x1fff, "Invalid full block");
        int index = getIndex(x, y, z);
        int oldBlock = blockIds[index];
        byte oldData = blockData.get(index);
        int newBlock = (value & 0x1ff0) >> 4;
        byte newData = (byte) (value & 0xf);
        if (oldBlock != newBlock) {
            blockIds[index] = newBlock;
            setDirty(true);
        }
        blockData.set(index, newData);
        return (oldBlock << 4) | oldData;
    }

    public int getFullBlock(int index) {
        return (blockIds[index] << 4) | blockData.get(index);
    }

    public int[] getBlockIds() {
        return Arrays.copyOf(blockIds, blockIds.length);
    }

    public byte[] getBlockData() {
        return blockData.getData();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public BlockStorage copy() {
        return new BlockStorage(Arrays.copyOf(blockIds, blockIds.length), blockData.copy());
    }
}
