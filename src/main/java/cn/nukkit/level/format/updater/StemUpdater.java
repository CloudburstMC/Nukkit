package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class StemUpdater implements Updater {
    private final Level level;
    private final ChunkSection section;
    private final int stemId;
    private final int productId;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public StemUpdater(Level level, ChunkSection section, int stemId, int productId) {
        this.level = level;
        this.section = section;
        this.stemId = stemId;
        this.productId = productId;
    }

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        if (state.getBlockId() != stemId) {
            return false;
        }

        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            int sideId = level.getBlockIdAt(
                    offsetX + x + blockFace.getXOffset(),
                    offsetY + y,
                    offsetZ + z + blockFace.getZOffset()
            );
            if (sideId == productId) {
                Block blockStem = state.getBlock(level, offsetX + x, offsetY + y, offsetZ + z, 0);
                ((Faceable) blockStem).setBlockFace(blockFace);
                section.setBlockStateAtLayer(x, y, z, 0, blockStem.getCurrentState());
                return true;
            }
        }

        return false;
    }
}
