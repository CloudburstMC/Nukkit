package cn.nukkit.level.format.anvil.util;

import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.utils.BinaryStream;
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
        this.setFullBlock(getIndex(x, y, z), (short) value);
    }

    public int getAndSetFullBlock(int x, int y, int z, int value) {
        return getAndSetFullBlock(getIndex(x, y, z), (short) value);
    }

    private int getAndSetFullBlock(int index, short value) {
        Preconditions.checkArgument(value < 0xfff, "Invalid full block");
        byte oldBlock = blockIds[index];
        byte oldData = blockData.get(index);
        byte newBlock = (byte) ((value & 0xff0) >> 4);
        byte newData = (byte) (value & 0xf);
        if (oldBlock != newBlock) {
            blockIds[index] = newBlock;
        }
        if (oldData != newData) {
            blockData.set(index, newData);
        }
        return ((oldBlock & 0xff) << 4) | oldData;
    }

    private int getFullBlock(int index) {
        byte block = blockIds[index];
        byte data = blockData.get(index);
        return ((block & 0xff) << 4) | data;
    }

    private void setFullBlock(int index, short value) {
        Preconditions.checkArgument(value < 0xfff, "Invalid full block");
        byte block = (byte) ((value & 0xff0) >> 4);
        byte data = (byte) (value & 0xf);

        blockIds[index] = block;
        blockData.set(index, data);
    }

    public byte[] getBlockIds() {
        return Arrays.copyOf(blockIds, blockIds.length);
    }

    public byte[] getBlockData() {
        return blockData.getData();
    }

    public void writeTo(BinaryStream stream) {
        PalettedBlockStorage storage = PalettedBlockStorage.createFromBlockPalette();
        for (int i = 0; i < SECTION_SIZE; i++) {
            storage.setBlock(i, GlobalBlockPalette.getOrCreateRuntimeId(blockIds[i] & 0xff, blockData.get(i)));
        }
        storage.writeTo(stream);
    }

    public BlockStorage copy() {
        return new BlockStorage(blockIds.clone(), blockData.copy());
    }
}