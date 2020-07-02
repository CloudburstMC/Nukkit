package cn.nukkit.level.format.anvil.util;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import com.google.common.base.Preconditions;

import java.util.Arrays;

public class BlockStorage {
    public static final int SECTION_SIZE = 4096;
    private final byte[] blockIds;
    private final byte[] blockIdsExtra;
    private final NibbleArray blockData;
    private final NibbleArray blockDataExtra;
    private final byte[] blockDataHyperA;
    private final short[] blockDataHyperB;
    private boolean hasBlockIds;
    private boolean hasBlockIdExtras;
    private boolean hasBlockDataExtras;
    private boolean hasBlockDataHyperA;
    private boolean hasBlockDataHyperB;

    public BlockStorage() {
        blockIds = new byte[SECTION_SIZE];
        blockIdsExtra = new byte[SECTION_SIZE];
        blockData = new NibbleArray(SECTION_SIZE);
        blockDataExtra = new NibbleArray(SECTION_SIZE);
        blockDataHyperA = new byte[SECTION_SIZE];
        blockDataHyperB = new short[SECTION_SIZE];
    }

    private BlockStorage(
            byte[] blockIds, byte[] blockIdsExtra, NibbleArray blockData, NibbleArray blockDataExtra,
            byte[] blockDataHyperA, short[] blockDataHyperB) {
        this.blockIds = blockIds;
        this.blockIdsExtra = blockIdsExtra;
        this.blockData = blockData;
        this.blockDataExtra = blockDataExtra;
        this.blockDataHyperA = blockDataHyperA;
        this.blockDataHyperB = blockDataHyperB;
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

        return getBlockData(getIndex(x, y, z));
    }
    
    private int getBlockData(int index) {
        int base = blockData.get(index) & 0xf;
        int extra = hasBlockDataExtras? ((blockDataExtra.get(index) & 0xF) << 4) : 0;
        int hyperA = hasBlockDataHyperA? ((blockDataHyperA[index] & 0xff) << 8) : 0;
        int hyperB = hasBlockDataHyperB? ((blockDataHyperB[index] & 0xffff) << 16) : 0; 
        return base | extra | hyperA | hyperB;
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

        return getBlockId(getIndex(x, y, z));
    }
    
    private int getBlockId(int index) {
        return (blockIds[index] & 0xFF) | (hasBlockIdExtras? (blockIdsExtra[index] & 0xFF) << 8 : 0);
    }
    
    public int getBlockIdBase(int x, int y, int z) {
        if (!hasBlockIds) {
            return 0;
        }
        
        int index = getIndex(x, y, z);
        return blockIds[index] & 0xFF;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public short getHyperDataB(int x, int y, int z) {
        if (!hasBlockDataHyperB) {
            return 0;
        }

        int index = getIndex(x, y, z);
        return blockDataHyperB[index];
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public byte getHyperDataA(int x, int y, int z) {
        if (!hasBlockDataHyperA) {
            return 0;
        }

        int index = getIndex(x, y, z);
        return blockDataHyperA[index];
    }
    
    public int getBlockIdExtra(int x, int y, int z) {
        if (!hasBlockIdExtras) {
            return 0;
        }
        
        int index = getIndex(x, y, z);
        return blockIdsExtra[index] & 0xFF;
    }

    public void setBlockId(int x, int y, int z, int id) {
        setBlockId(getIndex(x, y, z), id);
    }
    
    private void setBlockId(int index, int id) {
        byte blockBase = (byte) (id & 0xff);
        blockIds[index] = blockBase;
        
        byte extraBase = (byte) ((id >> 8) & 0xff);
        blockIdsExtra[index] = extraBase;
        
        hasBlockIdExtras |= extraBase != 0;
        hasBlockIds |= blockBase != 0 || hasBlockIdExtras;
    }
    
    public void setBlockData(int x, int y, int z, int data) {
        setBlockData(getIndex(x, y, z), data);
    }
    
    private void setBlockData(int index, int data) {
        byte data1 = (byte) (data & 0xF);
        byte data2 = (byte) (data >> 4 & 0xF);
        byte data3 = (byte) (data >> 8 & 0xFF);
        short data4 = (short) (data >> 16 & 0xFFFF); 
        blockData.set(index, data1);
        blockDataExtra.set(index, data2);
        blockDataHyperA[index] = data3;
        blockDataHyperB[index] = data4;
        
        hasBlockDataExtras |= data2 != 0;
        hasBlockDataHyperA |= data3 != 0;
        hasBlockDataHyperB |= data4 != 0;
        hasBlockIds |= data1 != 0 || hasBlockDataExtras || hasBlockDataHyperA || hasBlockDataHyperB;
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setBlock(int x, int y, int z, int id, int data) {
        int index = getIndex(x, y, z);
        setBlockId(index, id);
        setBlockData(index, data);
    }

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN")
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(getIndex(x, y, z));
    }

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN")
    public void setFullBlock(int x, int y, int z, int value) {
        this.setFullBlock(getIndex(x, y, z), value);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public int[] getAndSetBlock(int x, int y, int z, int id, int meta) {
        return getAndSetBlock(getIndex(x, y, z), id, meta);
    }
    
    private int[] getAndSetBlock(int index, int id, int meta) {
        int oldId = getBlockId(index);
        int oldData = getBlockData(index);
        setBlockId(index, id);
        setBlockData(index, meta);
        return new int[] {oldId, oldData};
    }
    
    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN", replaceWith = "getAndSetFullBlock")
    public int getAndSetFullBlock(int x, int y, int z, int value) {
        return getAndSetFullBlock(getIndex(x, y, z), value);
    }

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN")
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
        blockDataHyperA[index] = 0;
        blockDataHyperB[index] = 0;
        hasBlockIdExtras |= newBlockExtra != 0;
        hasBlockDataExtras |= newDataExtra != 0;
        hasBlockIds |= newBlock != 0 || hasBlockIdExtras || hasBlockDataExtras;
        
        return (oldBlockExtra & 0xff) << Block.DATA_BITS + 8 | (oldBlock & 0xff) << Block.DATA_BITS | (oldDataExtra & 0xF) << 4 | oldData;
    }

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN")
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

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    private int[] getBlockState(int index) {
        if (!hasBlockIds) {
            return new int[]{0,0};
        }
        return new int[]{getBlockId(index), getBlockData(index)};
    }

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN")
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
        blockDataHyperA[index] = 0;
        blockDataHyperB[index] = 0;
        
        hasBlockIdExtras |= extra != 0;
        hasBlockDataExtras |= dataExtra != 0;
        hasBlockIds |= block != 0 || hasBlockIdExtras || hasBlockDataExtras;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public int[] getBlockState(int x, int y, int z) {
        return getBlockState(getIndex(x, y, z));
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

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public byte[] getBlockDataHyperA() {
        if (hasBlockDataHyperB) {
            return Arrays.copyOf(blockDataHyperA, blockDataHyperA.length);
        } else {
            return new byte[SECTION_SIZE];
        }
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public short[] getBlockDataHyperB() {
        if (hasBlockDataHyperB) {
            return Arrays.copyOf(blockDataHyperB, blockDataHyperB.length);
        } else {
            return new short[SECTION_SIZE];
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
            if (hasBlockDataExtras || hasBlockDataHyperA || hasBlockDataHyperB) {
                for (int i = 0; i < SECTION_SIZE; i++) {
                    data[i] = blockData.get(i) & 0xF | ((blockDataExtra.get(i) & 0xF) << 4) | ((blockDataHyperA[i] & 0xFF) << 8) | ((blockDataHyperB[i] & 0xFFFF) << 16);
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
        boolean hasMeta = false;
        for (byte blockId : blockIdsExtra) {
            if (blockId != 0) {
                hasBlockIdExtras = true;
                hasMeta = true;
                break;
            }
        }

        for (short data : blockDataHyperB) {
            if (data != 0) {
                hasBlockDataHyperB = true;
                hasMeta = true;
                break;
            }
        }

        for (short data : blockDataHyperA) {
            if (data != 0) {
                hasBlockDataHyperA = true;
                hasMeta = true;
                break;
            }
        }
        
        for (byte dataId : blockDataExtra.getData()) {
            if (dataId != 0) {
                hasBlockDataExtras = true;
                hasMeta = true;
                break;
            }
        }
    
        if (hasMeta) {
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
        return new BlockStorage(blockIds.clone(), blockIdsExtra.clone(), blockData.copy(), blockDataExtra.copy(), blockDataHyperA.clone(), blockDataHyperB.clone());
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

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean hasBlockDataHyperA() {
        return hasBlockDataHyperA;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean hasBlockDataHyperB() {
        return hasBlockDataHyperB;
    }
}
