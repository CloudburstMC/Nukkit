package cn.nukkit.level.format.anvil.util;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.blockstate.MutableBlockState;
import com.google.common.base.Preconditions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class BlockStorage {
    private static final byte FLAG_HAS_ID           = 0b00001;
    private static final byte FLAG_HAS_ID_EXTRA     = 0b00010;
    private static final byte FLAG_HAS_DATA_EXTRA   = 0b00100;
    private static final byte FLAG_HAS_DATA_HYPER_A = 0b01000;
    private static final byte FLAG_HAS_DATA_HYPER_B = 0b10000;
    
    private static final byte FLAG_ENABLE_ID_EXTRA = FLAG_HAS_ID | FLAG_HAS_ID_EXTRA;
    private static final byte FLAG_ENABLE_DATA_EXTRA = FLAG_HAS_ID | FLAG_HAS_DATA_EXTRA;
    private static final byte FLAG_ENABLE_DATA_HYPER_A = FLAG_ENABLE_DATA_EXTRA | FLAG_HAS_DATA_HYPER_A;
    private static final byte FLAG_ENABLE_DATA_HYPER_B = FLAG_ENABLE_DATA_HYPER_A | FLAG_HAS_DATA_HYPER_B;
    
    private static final byte FLAG_EVERYTHING_ENABLED = FLAG_ENABLE_DATA_HYPER_B | FLAG_ENABLE_ID_EXTRA;
    
    private static final int BLOCK_ID_MASK          = 0x00FF;
    private static final int BLOCK_ID_EXTRA_MASK    = 0xFF00;
    private static final int BLOCK_ID_FULL          = BLOCK_ID_MASK | BLOCK_ID_EXTRA_MASK;
    
    private static final int BLOCK_DATA_MASK        = 0x000F;
    private static final int BLOCK_DATA_EXTRA_MASK  = 0x00F0;
    private static final int BLOCK_DATA_EXTRA_HYPER = 0xFF00;

    public static final int SECTION_SIZE = 4096;
    private static final MutableBlockState[] EMPTY = new MutableBlockState[SECTION_SIZE];
    static {
        Arrays.fill(EMPTY, BlockStateRegistry.getDefault(BlockID.AIR));
    }
    
    
    private final MutableBlockState[] states;
    private byte flags;

    public BlockStorage() {
        states = EMPTY.clone();
    }

    private BlockStorage(MutableBlockState[] states, byte flags) {
        this.states = states;
        this.flags = flags;
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

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", since = "1.3.0.0-PN")
    private void setFullBlock(int index, int value) {
        Preconditions.checkArgument(value < (BLOCK_ID_FULL << Block.DATA_BITS | Block.DATA_MASK), "Invalid full block");
        int blockId = value >> Block.DATA_BITS & BLOCK_ID_FULL;
        int data = value & Block.DATA_MASK;
        MutableBlockState state = BlockStateRegistry.getFromIntMeta(blockId, data);
        setBlockState(index, state);
    }

    private void setBlockState(int index, MutableBlockState state) {
        states[index] = state;
        updateFlags(state);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public MutableBlockState getBlockState(int x, int y, int z) {
        return getBlockState(getIndex(x, y, z));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private MutableBlockState getBlockState(int index) {
        return states[index].copy();
    }

    public void recheckBlocks() {
        flags = computeFlags((byte) 0, states);
    }
    
    private void updateFlags(MutableBlockState state) {
        if (flags != FLAG_EVERYTHING_ENABLED) {
            flags = computeFlags(flags, state);
        }
    }
    
    private byte computeFlags(byte newFlags, MutableBlockState... states) {
        for (MutableBlockState state : states) {
            int blockId = state.getBlockId();
            if ((blockId & BLOCK_ID_EXTRA_MASK) != 0) {
                newFlags |= FLAG_ENABLE_ID_EXTRA;
            } else if (blockId != 0) {
                newFlags |= FLAG_HAS_ID;
            }

            int bitSize = state.getProperties().getBitSize();
            if (bitSize > 16) {
                newFlags |= FLAG_ENABLE_DATA_HYPER_B;
            } else if (bitSize > 8) {
                newFlags |= FLAG_HAS_DATA_HYPER_A;
            } else if (bitSize > 4) {
                newFlags |= FLAG_HAS_DATA_EXTRA;
            } else if (bitSize > 0) {
                newFlags |= FLAG_HAS_ID;
            }

            if (newFlags == FLAG_EVERYTHING_ENABLED) {
                return newFlags;
            }
        }
        
        return newFlags; 
    }

    public BlockStorage copy() {
        return new BlockStorage(states.clone(), flags);
    }
    
    private boolean getFlag(byte flag) {
        return (flags & flag) == flag;
    }

    public boolean hasBlockIds() {
        return getFlag(FLAG_HAS_ID);
    }

    public boolean hasBlockIdExtras() {
        return getFlag(FLAG_HAS_ID_EXTRA);
    }

    public boolean hasBlockDataExtras() {
        return getFlag(FLAG_HAS_DATA_EXTRA);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean hasBlockDataHyperA() {
        return getFlag(FLAG_HAS_DATA_HYPER_A);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean hasBlockDataHyperB() {
        return getFlag(FLAG_HAS_DATA_HYPER_B);
    }
}
