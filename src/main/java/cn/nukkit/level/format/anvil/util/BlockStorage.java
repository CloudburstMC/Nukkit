package cn.nukkit.level.format.anvil.util;

import cn.nukkit.block.Block;
import com.google.common.base.Preconditions;

import java.util.Arrays;

public class BlockStorage {
    public static final int SECTION_SIZE = 4096;
    private final byte[] blockIds;
    private final byte[] blockIdsExtra;
    private final NibbleArray blockData;
    private final NibbleArray blockDataExtra;
    private transient boolean hasBlockIds;
    private transient boolean hasBlockIdExtras;
    private transient boolean hasBlockDataExtras;

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
        recheckBlocks();
    }

    private static int getIndex(int x, int y, int z) {
        int index = (x << 8) + (z << 4) + y; // XZY = Bedrock format
        Preconditions.checkArgument(index >= 0 && index < SECTION_SIZE, "Invalid index");
        return index;
    }

    public int getBlockData(int x, int y, int z) {
        if (!hasBlockIds) {
            return 0;
        }
        
        int index = getIndex(x, y, z);
        int base = blockData.get(index) & 0xf;
        int extra = hasBlockDataExtras? blockDataExtra.get(index) & 0xF : 0;
        return (extra << 4) | base;
    }
    
    public int getBlockDataExtra(int x, int y, int z) {
        if (!hasBlockDataExtras) {
            return 0;
        }
        
        int index = getIndex(x, y, z);
        return blockDataExtra.get(index) & 0xF;
    }
    
    public int getBlockDataBase(int x, int y, int z) {
        if (!hasBlockIds) {
            return 0;
        }
        
        int index = getIndex(x, y, z);
        return blockData.get(index) & 0xf;
    }

    public int getBlockId(int x, int y, int z) {
        if (!hasBlockIds) {
            return 0;
        }
        
        int index = getIndex(x, y, z);
        return (blockIds[index] & 0xFF) | (hasBlockIdExtras? (blockIdsExtra[index] & 0xFF) << 8 : 0);
    }
    
    public int getBlockIdBase(int x, int y, int z) {
        if (!hasBlockIds) {
            return 0;
        }
        
        int index = getIndex(x, y, z);
        return blockIds[index] & 0xFF;
    }
    
    public int getBlockIdExtra(int x, int y, int z) {
        if (!hasBlockIdExtras) {
            return 0;
        }
        
        int index = getIndex(x, y, z);
        return blockIdsExtra[index] & 0xFF;
    }

    public void setBlockId(int x, int y, int z, int id) {
        int index = getIndex(x, y, z);
        
        byte blockBase = (byte) (id & 0xff);
        blockIds[index] = blockBase;
        
        byte extraBase = (byte) ((id >> 8) & 0xff);
        blockIdsExtra[index] = extraBase;
        
        hasBlockIdExtras |= extraBase != 0;
        hasBlockIds |= blockBase != 0 | hasBlockIdExtras;
    }

    public void setBlockData(int x, int y, int z, int data) {
        int index = getIndex(x, y, z);
        byte data1 = (byte) (data & 0xF);
        byte data2 = (byte) (data >> 4 & Block.DATA_MASK >> 4 & 0xF);
        blockData.set(index, data1);
        blockDataExtra.set(index, data2);
        
        hasBlockDataExtras |= data2 > 0;
        hasBlockIds |= data1 != 0 | hasBlockDataExtras;
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
        byte oldBlockExtra = hasBlockIdExtras? blockIdsExtra[index] : 0;
        byte oldBlock = hasBlockIds? blockIds[index] : 0;
        byte oldData = hasBlockIds? blockData.get(index) : 0;
        byte oldDataExtra = hasBlockDataExtras? blockDataExtra.get(index) : 0;
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
        hasBlockIdExtras |= newBlockExtra != 0;
        hasBlockDataExtras |= newDataExtra != 0;
        hasBlockIds |= newBlock != 0 | hasBlockIdExtras | hasBlockDataExtras;
        
        return (oldBlockExtra & 0xff) << Block.DATA_BITS + 8 | (oldBlock & 0xff) << Block.DATA_BITS | (oldDataExtra & 0xF) << 4 | oldData;
    }

    private int getFullBlock(int index) {
        if (!hasBlockIds) {
            return 0;
        }
        byte block = blockIds[index];
        byte extra = hasBlockIdExtras ? blockIdsExtra[index] : 0;
        byte data = blockData.get(index);
        byte dataExtra = hasBlockDataExtras ? blockDataExtra.get(index) : 0;
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
        
        hasBlockIdExtras |= extra != 0;
        hasBlockDataExtras |= dataExtra != 0;
        hasBlockIds |= block != 0 | hasBlockIdExtras | hasBlockDataExtras;
    }

    public byte[] getBlockIds() {
        if (hasBlockIds) {
            return Arrays.copyOf(blockIds, blockIds.length);
        } else {
            return new byte[SECTION_SIZE];
        }
    }

    public byte[] getBlockIdsExtra() {
        if (hasBlockIdExtras) {
            return Arrays.copyOf(blockIdsExtra, blockIdsExtra.length);
        } else {
            return new byte[SECTION_SIZE];
        }
    }

    public byte[] getBlockData() {
        if (hasBlockIds) {
            return blockData.getData();
        } else {
            return new byte[SECTION_SIZE / 2];
        }
    }
    
    public byte[] getBlockDataExtra() {
        if (hasBlockDataExtras) {
            return blockDataExtra.getData();
        } else {
            return new byte[SECTION_SIZE / 2];
        }
    }
    
    public int[] getBlockIdsExtended() {
        int[] ids = new int[SECTION_SIZE];
        if (hasBlockIds) {
            if (hasBlockIdExtras) {
                for (int i = 0; i < SECTION_SIZE; i++) {
                    ids[i] = blockIds[i] & 0xFF | (blockIdsExtra[i] & 0xFF) << 8;
                }
            } else {
                for (int i = 0; i < SECTION_SIZE; i++) {
                    ids[i] = blockIds[i] & 0xFF;
                }
            }
        }
        return ids;
    }
    
    public int[] getBlockDataExtended() {
        int[] data = new int[SECTION_SIZE];
        if (hasBlockIds) {
            if (hasBlockDataExtras) {
                for (int i = 0; i < SECTION_SIZE; i++) {
                    data[i] = blockData.get(i) & 0xF | (blockDataExtra.get(i) & 0xF) << 4;
                }
            } else {
                for (int i = 0; i < SECTION_SIZE; i++) {
                    data[i] = blockData.get(i) & 0xF;
                }
            }
        }
        return data;
    }
    
    public void recheckBlocks() {
        for (byte blockId : blockIdsExtra) {
            if (blockId != 0) {
                hasBlockIdExtras = true;
                break;
            }
        }
    
        for (byte dataId : blockDataExtra.getData()) {
            if (dataId != 0) {
                hasBlockDataExtras = true;
                break;
            }
        }
    
        if (hasBlockDataExtras || hasBlockIdExtras) {
            hasBlockIds = true;
        } else {
            for (byte blockId : blockIds) {
                if (blockId != 0) {
                    hasBlockIds = true;
                    break;
                }
            }
        }
    }

    public BlockStorage copy() {
        return new BlockStorage(blockIds.clone(), blockIdsExtra.clone(), blockData.copy(), blockDataExtra.copy());
    }
    
    public boolean hasBlockIds() {
        return hasBlockIds;
    }
    
    public boolean hasBlockIdExtras() {
        return hasBlockIdExtras;
    }
    
    public boolean hasBlockDataExtras() {
        return hasBlockDataExtras;
    }
}
