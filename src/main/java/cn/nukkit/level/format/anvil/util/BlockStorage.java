package cn.nukkit.level.format.anvil.util;

import cn.nukkit.block.Block;
import com.google.common.base.Preconditions;

import java.util.Arrays;

public class BlockStorage {
    private static final int SECTION_SIZE = 4096;
    private final byte[] blockIds;
    private final byte[] blockIdsExtra;
    private final NibbleArray blockData;
    private final NibbleArray blockDataExtra;

    public BlockStorage() {
        blockIds = new byte[SECTION_SIZE];
        blockIdsExtra = new byte[SECTION_SIZE];
        blockData = new NibbleArray(SECTION_SIZE);
        blockDataExtra = new NibbleArray(SECTION_SIZE);
    }

    private BlockStorage(byte[] blockIds, byte[] blockIdsExtra, NibbleArray blockData, NibbleArray blockDataExtra) {
        this.blockIds = blockIds;
        this.blockIdsExtra = blockIdsExtra;
        this.blockData = blockData;
        this.blockDataExtra = blockDataExtra;
    }

    private static int getIndex(int x, int y, int z) {
        int index = (x << 8) + (z << 4) + y; // XZY = Bedrock format
        Preconditions.checkArgument(index >= 0 && index < SECTION_SIZE, "Invalid index");
        return index;
    }

    public int getBlockData(int x, int y, int z) {
        int index = getIndex(x, y, z);
        int base = blockData.get(index) & 0xf;
        int extra = blockDataExtra.get(index) & 0xf;
        return (extra << 4) | base;
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
        int index = getIndex(x, y, z);
        byte data1 = (byte) (data & 0xF);
        byte data2 = (byte) (data >> 4 & Block.DATA_MASK >> 4 & 0xF);
        blockData.set(index, data1);
        blockDataExtra.set(index, data2);
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
        Preconditions.checkArgument(value < (0x1FF << Block.DATA_BITS | Block.DATA_MASK), "Invalid full block");
        byte oldBlockExtra = blockIdsExtra[index];
        byte oldBlock = blockIds[index];
        byte oldData = blockData.get(index);
        byte oldDataExtra = blockDataExtra.get(index);
        byte newBlockExtra = (byte) ((value >> (Block.DATA_BITS + 8)) & 0xFF);
        byte newBlock = (byte) ((value >> Block.DATA_BITS) & 0xFF);
        byte newData = (byte) (value & 0xf);
        byte newDataExtra = (byte) (value >> 4 & Block.DATA_MASK >> 4 & 0xF);
        if (oldBlock != newBlock) {
            blockIds[index] = newBlock;
        }
        if (oldBlockExtra != newBlockExtra) {
            blockIdsExtra[index] = newBlockExtra;
        }
        if (oldData != newData) {
            blockData.set(index, newData);
        }
        if (oldDataExtra != newDataExtra) {
            blockDataExtra.set(index, newDataExtra);
        }
        return (oldBlockExtra & 0xff) << Block.DATA_BITS + 8 | (oldBlock & 0xff) << Block.DATA_BITS | (oldDataExtra & 0xF) << 4 | oldData;
    }

    private int getFullBlock(int index) {
        byte block = blockIds[index];
        byte extra = blockIdsExtra[index];
        byte data = blockData.get(index);
        byte dataExtra = blockDataExtra.get(index);
        return (extra & 0xff) << Block.DATA_BITS + 8 | ((block & 0xff) << Block.DATA_BITS) | ((dataExtra & 0xF) << 4) | data;
    }

    private void setFullBlock(int index, int value) {
        Preconditions.checkArgument(value < (0x1FF << Block.DATA_BITS | Block.DATA_MASK), "Invalid full block");
        byte extra = (byte) ((value >> (Block.DATA_BITS + 8)) & 0xFF);
        byte block = (byte) ((value >> Block.DATA_BITS) & 0xFF);
        byte dataExtra = (byte) (value >> 4 & Block.DATA_MASK >> 4 & 0xF);
        byte data = (byte) (value & 0xf);

        blockIds[index] = block;
        blockIdsExtra[index] = extra;
        blockData.set(index, data);
        blockDataExtra.set(index, dataExtra);
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
    
    public byte[] getBlockDataExtra() {
        return blockDataExtra.getData();
    }
    
    public int[] getBlockIdsExtended() {
        int[] ids = new int[SECTION_SIZE];
        for (int i = 0; i < SECTION_SIZE; i++) {
            ids[i] = blockIds[i] & 0xFF | (blockIdsExtra[i] & 0xFF) << 8;
        }
        return ids;
    }
    
    public int[] getBlockDataExtended() {
        int[] data = new int[SECTION_SIZE];
        for (int i = 0; i < SECTION_SIZE; i++) {
            data[i] = blockData.get(i) & 0xF | (blockDataExtra.get(i) & 0xF) << 4;
        }
        return data;
    }

    public BlockStorage copy() {
        return new BlockStorage(blockIds.clone(), blockIdsExtra.clone(), blockData.copy(), blockDataExtra.copy());
    }
}
