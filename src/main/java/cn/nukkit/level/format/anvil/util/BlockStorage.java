package cn.nukkit.level.format.anvil.util;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public class BlockStorage {
    private static final int SECTION_SIZE = 4096;
    private final byte[] blockIds;
    private final NibbleArray blockData;
    private final NibbleArray matrix; //hack

    public BlockStorage() {
        blockIds = new byte[SECTION_SIZE];
        blockData = new NibbleArray(SECTION_SIZE);
        matrix = new NibbleArray(SECTION_SIZE);
    }

    private BlockStorage(byte[] blockIds, NibbleArray blockData, NibbleArray matrix) {
        this.blockIds = blockIds;
        this.blockData = blockData;
        this.matrix = matrix;
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
        return (blockIds[index] & 0xff) + 256 * (matrix.get(index) & 1);
    }

    public void setBlockId(int x, int y, int z, int id) {
        int index = getIndex(x, y, z);
        matrix.set(index, (byte) ((id / 256) & 1));
        blockIds[index] = (byte) (id & 0xff);
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
        int oldBlock = (blockIds[index] & 0xff) + 256 * (matrix.get(index) & 1);
        byte oldData = blockData.get(index);
        int newBlock = (value & 0x1ff0) >> 4;
        byte newData = (byte) (value & 0xf);
        if (oldBlock != newBlock) {
            matrix.set(index, (byte) ((newBlock / 256) & 1));
            blockIds[index] = (byte) (newBlock & 0xff);
        }
        if (oldData != newData) {
            blockData.set(index, newData);
        }
        return (oldBlock << 4) | oldData;
    }

    public int getFullBlock(int index) {
        return (((blockIds[index] & 0xff) + 256 * (matrix.get(index) & 1)) << 4) | blockData.get(index);
    }

    private void setFullBlock(int index, short value) {
        Preconditions.checkArgument(value < 0x1fff, "Invalid full block");
        int block = (value & 0x1ff0) >> 4;
        byte data = (byte) (value & 0xf);
        matrix.set(index, (byte) ((block / 256) & 1));
        blockIds[index] = (byte) (block & 0xff);
        blockData.set(index, data);
    }

    public int getMatrixElement(int x, int y, int z) {
        return matrix.get(getIndex(x, y, z)) & 1;
    }

    public void setMatrixElement(int x, int y, int z, int element) {
        matrix.set(getIndex(x, y, z), (byte) (element & 1));
    }

    public byte[] getBlockIds() {
        return Arrays.copyOf(blockIds, blockIds.length);
    }

    public byte[] getBlockData() {
        return blockData.getData();
    }

    public byte[] getMatrix() {
        return matrix.getData();
    }

    public BlockStorage copy() {
        return new BlockStorage(Arrays.copyOf(blockIds, blockIds.length), blockData.copy(), matrix.copy());
    }
}
