package cn.nukkit.level;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockStateRegistry;
import lombok.extern.log4j.Log4j2;

@Deprecated
@DeprecationDetails(reason = "Reimplemented using BlockState", replaceWith = "BlockStateRegistry", since = "1.4.0.0-PN")
@Log4j2
public class GlobalBlockPalette {
    @Deprecated
    @DeprecationDetails(reason = "Public mutable array", replaceWith = "BlockStateRegistry.getBlockPaletteBytes() or BlockStateRegistry.copyBlockPaletteBytes()", since = "1.4.0.0-PN")
    public static final byte[] BLOCK_PALETTE = BlockStateRegistry.getBlockPaletteBytes();

    @Deprecated
    @DeprecationDetails(reason = "Limited to 32 bits meta", since = "1.4.0.0-PN", replaceWith = "BlockStateRegistry.getRuntimeId(BlockState)")
    public static int getOrCreateRuntimeId(int id, int meta) {
        return BlockStateRegistry.getRuntimeId(id, meta);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockStateRegistry.getRuntimeId(BlockState)", since = "1.3.0.0-PN")
    public static int getOrCreateRuntimeId(int legacyId) {
        return getOrCreateRuntimeId(legacyId >> Block.DATA_BITS, legacyId & Block.DATA_MASK);
    }

    @Deprecated
    @DeprecationDetails(reason = "Moved to BlockStateRegistry", replaceWith = "BlockStateRegistry.getPersistenceName(int)", since = "1.3.0.0-PN")
    public static String getName(int blockId) {
        return BlockStateRegistry.getPersistenceName(blockId);
    }
}
