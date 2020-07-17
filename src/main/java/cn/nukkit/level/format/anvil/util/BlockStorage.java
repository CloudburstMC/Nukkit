package cn.nukkit.level.format.anvil.util;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.functional.BlockPositionDataConsumer;
import com.google.common.base.Preconditions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class BlockStorage {
    private static final byte FLAG_HAS_ID           = 0b00_0001;
    private static final byte FLAG_HAS_ID_EXTRA     = 0b00_0010;
    private static final byte FLAG_HAS_DATA_EXTRA   = 0b00_0100;
    private static final byte FLAG_HAS_DATA_BIG     = 0b00_1000;
    private static final byte FLAG_HAS_DATA_HUGE    = 0b01_0000;
    private static final byte FLAG_PALETTE_UPDATED  = 0b10_0000;
    
    private static final byte FLAG_ENABLE_ID_EXTRA  = FLAG_HAS_ID | FLAG_HAS_ID_EXTRA;
    private static final byte FLAG_ENABLE_DATA_EXTRA= FLAG_HAS_ID | FLAG_HAS_DATA_EXTRA;
    private static final byte FLAG_ENABLE_DATA_BIG  = FLAG_ENABLE_DATA_EXTRA | FLAG_HAS_DATA_BIG;
    private static final byte FLAG_ENABLE_DATA_HUGE = FLAG_ENABLE_DATA_BIG | FLAG_HAS_DATA_HUGE;
    
    private static final byte FLAG_EVERYTHING_ENABLED = FLAG_ENABLE_DATA_HUGE | FLAG_ENABLE_ID_EXTRA | FLAG_PALETTE_UPDATED;
    
    private static final int BLOCK_ID_MASK          = 0x00FF;
    private static final int BLOCK_ID_EXTRA_MASK    = 0xFF00;
    private static final int BLOCK_ID_FULL          = BLOCK_ID_MASK | BLOCK_ID_EXTRA_MASK;
    
    public static final int SECTION_SIZE = 4096;
    
    private static final BlockState[] EMPTY = new BlockState[SECTION_SIZE];
    static {
        Arrays.fill(EMPTY, BlockState.AIR);
    }
    
    private final PalettedBlockStorage palette;
    private final BlockState[] states;
    private byte flags = FLAG_PALETTE_UPDATED;

    public BlockStorage() {
        states = EMPTY.clone();
        palette = new PalettedBlockStorage();
    }

    private BlockStorage(BlockState[] states, byte flags, PalettedBlockStorage palette) {
        this.states = states;
        this.flags = flags;
        this.palette = palette;
    }

    private static int getIndex(int x, int y, int z) {
        int index = (x << 8) + (z << 4) + y; // XZY = Bedrock format
        Preconditions.checkArgument(index >= 0 && index < SECTION_SIZE, "Invalid index");
        return index;
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    public int getBlockData(int x, int y, int z) {
        return states[getIndex(x, y, z)].getBigDamage();
    }
    
    public int getBlockId(int x, int y, int z) {
        return states[getIndex(x, y, z)].getBlockId();
    }
    
    public void setBlockId(int x, int y, int z, int id) {
        int index = getIndex(x, y, z);
        setBlockState(index, states[index].withBlockId(id));    
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    public void setBlockData(int x, int y, int z, int data) {
        int index = getIndex(x, y, z);
        setBlockState(index, states[index].withData(data));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setBlock(int x, int y, int z, int id, int data) {
        int index = getIndex(x, y, z);
        BlockState state = BlockState.of(id, data);
        setBlockState(index, state);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(getIndex(x, y, z));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    public void setFullBlock(int x, int y, int z, int value) {
        this.setFullBlock(getIndex(x, y, z), value);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public BlockState getAndSetBlock(int x, int y, int z, int id, int meta) {
        return getAndSetBlockState(getIndex(x, y, z), BlockState.of(id, meta));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState getAndSetBlockState(int x, int y, int z, BlockState state) {
        return getAndSetBlockState(getIndex(x, y, z), state);
    }
    
    private BlockState getAndSetBlockState(int index, BlockState state) {
        BlockState old = states[index];
        setBlockState(index, state);
        return old;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setBlockState(int x, int y, int z, BlockState state) {
        setBlockState(getIndex(x, y, z), state);
    }
    
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "getAndSetFullBlock")
    public int getAndSetFullBlock(int x, int y, int z, int value) {
        return getAndSetFullBlock(getIndex(x, y, z), value);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    private int getAndSetFullBlock(int index, int value) {
        Preconditions.checkArgument(value < (0x1FF << Block.DATA_BITS | Block.DATA_MASK), "Invalid full block");
        int blockId = value >> Block.DATA_BITS & BLOCK_ID_FULL;
        int data = value & Block.DATA_MASK;
        BlockState newState = BlockState.of(blockId, data);
        BlockState oldState = states[index];
        if (oldState.equals(newState)) {
            return value;
        }
        setBlockState(index, newState);
        return oldState.getFullId();
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    private int getFullBlock(int index) {
        return states[index].getFullId();
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    private void setFullBlock(int index, int value) {
        Preconditions.checkArgument(value < (BLOCK_ID_FULL << Block.DATA_BITS | Block.DATA_MASK), "Invalid full block");
        int blockId = value >> Block.DATA_BITS & BLOCK_ID_FULL;
        int data = value & Block.DATA_MASK;
        BlockState state = BlockState.of(blockId, data);
        setBlockState(index, state);
    }

    private void setBlockState(int index, BlockState state) {
        if (states[index].equals(state)) {
            return;
        }
        
        states[index] = state;
        updateFlags(state);
        try {
            palette.setBlock(index, state.getRuntimeId());
        } catch (Exception ignored) {
            // This allow the API to be used before the Block.init() gets called, useful for testing or usage on early
            // states of the server initialization
            setFlag(FLAG_PALETTE_UPDATED, false);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState getBlockState(int x, int y, int z) {
        return states[getIndex(x, y, z)];
    }

    public void recheckBlocks() {
        flags = computeFlags((byte)(flags & FLAG_PALETTE_UPDATED), states);
    }
    
    private void updateFlags(BlockState state) {
        if (flags != FLAG_EVERYTHING_ENABLED) {
            flags = computeFlags(flags, state);
        }
    }
    
    private byte computeFlags(byte newFlags, BlockState... states) {
        for (BlockState state : states) {
            int blockId = state.getBlockId();
            if ((blockId & BLOCK_ID_EXTRA_MASK) != 0) {
                newFlags |= FLAG_ENABLE_ID_EXTRA;
            } else if (blockId != 0) {
                newFlags |= FLAG_HAS_ID;
            }

            int bitSize = state.getBitSize();
            if (bitSize > 16) {
                newFlags |= FLAG_ENABLE_DATA_HUGE;
            } else if (bitSize > 8) {
                newFlags |= FLAG_ENABLE_DATA_BIG;
            } else if (bitSize > 4) {
                newFlags |= FLAG_ENABLE_DATA_EXTRA;
            } else if (bitSize > 1 || blockId != 0) {
                newFlags |= FLAG_HAS_ID;
            }

            if (newFlags == FLAG_EVERYTHING_ENABLED) {
                return newFlags;
            }
        }
        
        return newFlags; 
    }

    public BlockStorage copy() {
        return new BlockStorage(states.clone(), flags, palette.copy());
    }
    
    private boolean getFlag(byte flag) {
        return (flags & flag) == flag;
    }
    
    private void setFlag(byte flag, boolean value) {
        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
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
    @Since("1.4.0.0-PN")
    public boolean hasBlockDataBig() {
        return getFlag(FLAG_HAS_DATA_BIG);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasBlockDataHuge() {
        return getFlag(FLAG_HAS_DATA_HUGE);
    }
    
    private boolean isPaletteUpdated() {
        return getFlag(FLAG_PALETTE_UPDATED);
    }

    public void writeTo(BinaryStream stream) {
        if (!isPaletteUpdated()) {
            for (int i = 0; i < states.length; i++) {
                palette.setBlock(i, states[i].getRuntimeId());
            }
            setFlag(FLAG_PALETTE_UPDATED, true);
        }
        palette.writeTo(stream);
    }

    public void iterateStates(BlockPositionDataConsumer<BlockState> consumer) {
        for (int i = 0; i < states.length; i++) {
            // XZY = Bedrock format
            //int index = (x << 8) + (z << 4) + y; // XZY = Bedrock format
            int x = (i >> 8) & 0xF;
            int z = (i >> 4) & 0xF;
            int y = i & 0xF;
            consumer.accept(x, y, z, states[i]);
        }
    }
}
