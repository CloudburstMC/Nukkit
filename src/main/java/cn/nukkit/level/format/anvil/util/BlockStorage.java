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
        this.blockIds = new byte[BlockStorage.SECTION_SIZE];
        this.blockData = new NibbleArray(BlockStorage.SECTION_SIZE);
    }

    public BlockStorage(final byte[] blockIds, final NibbleArray blockData) {
        this.blockIds = blockIds;
        this.blockData = blockData;
    }

    private static int getIndex(final int x, final int y, final int z) {
        final int index = (x << 8) + (z << 4) + y; // XZY = Bedrock format
        Preconditions.checkArgument(index >= 0 && index < BlockStorage.SECTION_SIZE, "Invalid index");
        return index;
    }

    public int getBlockData(final int x, final int y, final int z) {
        return this.blockData.get(BlockStorage.getIndex(x, y, z)) & 0xf;
    }

    public int getBlockId(final int x, final int y, final int z) {
        return this.blockIds[BlockStorage.getIndex(x, y, z)] & 0xFF;
    }

    public void setBlockId(final int x, final int y, final int z, final int id) {
        this.blockIds[BlockStorage.getIndex(x, y, z)] = (byte) (id & 0xff);
    }

    public void setBlockData(final int x, final int y, final int z, final int data) {
        this.blockData.set(BlockStorage.getIndex(x, y, z), (byte) data);
    }

    public int getFullBlock(final int x, final int y, final int z) {
        return this.getFullBlock(BlockStorage.getIndex(x, y, z));
    }

    public void setFullBlock(final int x, final int y, final int z, final int value) {
        this.setFullBlock(BlockStorage.getIndex(x, y, z), (short) value);
    }

    public int getAndSetFullBlock(final int x, final int y, final int z, final int value) {
        return this.getAndSetFullBlock(BlockStorage.getIndex(x, y, z), (short) value);
    }

    public byte[] getBlockIds() {
        return Arrays.copyOf(this.blockIds, this.blockIds.length);
    }

    public byte[] getBlockData() {
        return this.blockData.getData();
    }

    public void writeTo(final BinaryStream stream) {
        final PalettedBlockStorage storage = new PalettedBlockStorage();
        for (int i = 0; i < BlockStorage.SECTION_SIZE; i++) {
            storage.setBlock(i, GlobalBlockPalette.getOrCreateRuntimeId(this.blockIds[i] & 0xff, this.blockData.get(i)));
        }
        storage.writeTo(stream);
    }

    public BlockStorage copy() {
        return new BlockStorage(this.blockIds.clone(), this.blockData.copy());
    }

    private int getAndSetFullBlock(final int index, final short value) {
        Preconditions.checkArgument(value < 0xfff, "Invalid full block");
        final byte oldBlock = this.blockIds[index];
        final byte oldData = this.blockData.get(index);
        final byte newBlock = (byte) ((value & 0xff0) >> 4);
        final byte newData = (byte) (value & 0xf);
        if (oldBlock != newBlock) {
            this.blockIds[index] = newBlock;
        }
        if (oldData != newData) {
            this.blockData.set(index, newData);
        }
        return (oldBlock & 0xff) << 4 | oldData;
    }

    private int getFullBlock(final int index) {
        final byte block = this.blockIds[index];
        final byte data = this.blockData.get(index);
        return (block & 0xff) << 4 | data;
    }

    private void setFullBlock(final int index, final short value) {
        Preconditions.checkArgument(value < 0xfff, "Invalid full block");
        final byte block = (byte) ((value & 0xff0) >> 4);
        final byte data = (byte) (value & 0xf);

        this.blockIds[index] = block;
        this.blockData.set(index, data);
    }



}