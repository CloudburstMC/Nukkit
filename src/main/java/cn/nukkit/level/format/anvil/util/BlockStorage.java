package cn.nukkit.level.format.anvil.util;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.functional.BlockPositionDataConsumer;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.BitSet;

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
    @Nullable
    private BitSet denyStates = null;

    public BlockStorage() {
        states = EMPTY.clone();
        palette = new PalettedBlockStorage();
    }

    private BlockStorage(BlockState[] states, byte flags, PalettedBlockStorage palette, @Nullable BitSet denyStates) {
        this.states = states;
        this.flags = flags;
        this.palette = palette;
        this.denyStates = denyStates;
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
        return setBlockState(getIndex(x, y, z), BlockState.of(id, meta));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState getAndSetBlockState(int x, int y, int z, BlockState state) {
        return setBlockState(getIndex(x, y, z), state);
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

    private BlockState setBlockState(int index, BlockState state) {
        BlockState previous = states[index];
        if (previous.equals(state)) {
            return previous;
        }
        
        states[index] = state;
        updateFlags(index, previous, state);
        if (getFlag(FLAG_PALETTE_UPDATED)) {
            try {
                palette.setBlock(index, state.getRuntimeId());
            } catch (Exception ignored) {
                // This allow the API to be used before the Block.init() gets called, useful for testing or usage on early
                // states of the server initialization
                setFlag(FLAG_PALETTE_UPDATED, false);
            }
        }
        
        return previous;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void delayPaletteUpdates() {
        setFlag(FLAG_PALETTE_UPDATED, false);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState getBlockState(int x, int y, int z) {
        return states[getIndex(x, y, z)];
    }

    public void recheckBlocks() {
        flags = computeFlags((byte)(flags & FLAG_PALETTE_UPDATED), states);
    }
    
    private void updateFlags(int index, BlockState previous, BlockState state) {
        if (flags != FLAG_EVERYTHING_ENABLED) {
            flags = computeFlags(flags, state);
        }
        
        if (denyStates != null) {
            switch (previous.getBlockId()) {
                case BlockID.DENY:
                    clearDeny(index);
                    break;
                case BlockID.ALLOW:
                    clearAllow(index);
                    break;
                case BlockID.BORDER_BLOCK:
                    clearBorder(index);
                    break;
                default:
            }
        }
        
        switch (state.getBlockId()) {
            case BlockID.DENY:
                deny(index);
                break;
            case BlockID.BORDER_BLOCK:
                border(index);
                break;
            case BlockID.ALLOW:
                allow(index);
                break;
            default:
        }
    }

    private void border(int index) {
        if (denyStates == null) {
            denyStates = new BitSet();
        }
        index = (index & ~0xF) << 1;
        for (int y = 0; y <= 0xF; y++) {
            denyStates.set(index++);    // Deny
            denyStates.set(index++);    // Allow
                                        // Both deny and allow means border
        }
    }
    
    private void deny(int index) {
        if (denyStates == null) {
            denyStates = new BitSet();
        }
        int y = index & 0xF;
        index <<= 1;
        boolean first = true;
        for (; y <= 0xF; y++, index += 2, first = false) {
            if (denyStates.get(index + 1)) { //Allow
                if (first) { // Replacing an allow block with a deny block
                    if (denyStates.get(index)) { // If the XZ pos have a border, the border takes priority
                        return;
                    }
                    denyStates.clear(index + 1);
                } else if (states[index >> 1].getBlockId() == BlockID.ALLOW) {
                    // Check if the allow state is actually from a allow block or from a previous removal
                    return;
                } else {
                    denyStates.clear(index + 1);
                }
            }
            
            denyStates.set(index);
        }
    }
    
    private void allow(int index) {
        if (denyStates == null) {
            denyStates = new BitSet();
        }
        int y = index & 0xF;
        index <<= 1;
        boolean first = true;
        for (; y <= 0xF; y++, index += 2, first = false) {
            if (denyStates.get(index)) { //Deny
                if (first) { // Replacing a deny block with an allow block
                    if (denyStates.get(index + 1)) { // If the XZ pos have a border, the border takes priority
                        return;
                    }
                    denyStates.clear(index);
                } else if (states[index >> 1].getBlockId() == BlockID.DENY) {
                    // Check if the deny state is actually from a deny block or from a previous removal
                    return;
                } else {
                    denyStates.clear(index);
                }
            }

            denyStates.set(index + 1);
        }
    }
    
    private void clearAllow(int index) {
        assert denyStates != null;
        int y = index & 0xF;
        index <<= 1;
        for (; y <= 0xF; y++, index += 2, index++) {
            if (denyStates.get(index)) { // Deny or border
                break;
            }
            
            denyStates.clear(index + 1); // Remove the allow
        }
    }

    private void clearDeny(int index) {
        assert denyStates != null;
        int y = index & 0xF;
        index <<= 1;
        for (; y <= 0xF; y++, index += 2, index++) {
            if (denyStates.get(index + 1)) { // Allow or border
                break;
            }

            denyStates.clear(index); // Remove the deny
        }
    }
    
    private void clearBorder(final int index) {
        assert denyStates != null;
        
        // Check if there's an other border
        final int bottomIndex = index & ~0xF;
        final int topIndex = index | 0xF;
        for (int blockIndex = bottomIndex; blockIndex < topIndex; blockIndex++) {
            if (states[blockIndex].getBlockId() == BlockID.BORDER_BLOCK) {
                return;
            }
        }
        
        // Clear the border flags
        boolean removeDeny = true;
        boolean removeAllow = true;
        for (int blockIndex = bottomIndex, flagIndex = blockIndex << 1; blockIndex < topIndex; blockIndex++, flagIndex += 2) {
            switch (states[blockIndex].getBlockId()) {
                case BlockID.ALLOW:
                    removeDeny = true;
                    removeAllow = false;
                    break;
                case BlockID.DENY:
                    removeDeny = false;
                    removeAllow = true;
                    break;
                default:
            }
            if (removeDeny) {
                denyStates.clear(flagIndex);
            }
            if (removeAllow) {
                denyStates.clear(flagIndex + 1);
            }
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
        BitSet deny = denyStates;
        return new BlockStorage(states.clone(), flags, palette.copy(), (BitSet) (deny != null? deny.clone() : null));
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

    public int getBlockChangeStateAbove(int x, int y, int z) {
        BitSet denyFlags = this.denyStates;
        if (denyFlags == null) {
            return 0;
        }
        int index = getIndex(x, y, z) << 1;
        return (denyFlags.get(index)? 0x1 : 0x0) | (denyFlags.get(index + 1)? 0x2 : 0x0);
    }
}
