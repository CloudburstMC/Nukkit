package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.math.BlockFace;
import lombok.RequiredArgsConstructor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
public class BeehiveUpdater implements Updater {
    private final ChunkSection section;
    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        int blockId = state.getBlockId();
        if (blockId == BlockID.BEEHIVE || blockId == BlockID.BEE_NEST) {
            @SuppressWarnings("deprecation") 
            int meta = state.getLegacyDamage();
            BlockFace face = BlockFace.fromIndex(meta & 0b111);
            meta = ((meta & ~0b111) >> 1) | face.getHorizontalIndex();
            section.setBlockState(x, y, z, state.withData(meta));
            return true;
        }
        return false;
    }
}
