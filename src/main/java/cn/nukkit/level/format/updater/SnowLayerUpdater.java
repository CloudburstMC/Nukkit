package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSnowLayer;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class SnowLayerUpdater implements Updater {
    private final Level level;
    private final ChunkSection section;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SnowLayerUpdater(Level level, ChunkSection section) {
        this.level = level;
        this.section = section;
    }

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        if (state.getBlockId() != BlockID.SNOW_LAYER) {
            return false;
        }
        
        if (y > 0) {
            if (section.getBlockId(x, y-1, z) == BlockID.GRASS) {
                section.setBlockState(x, y, z, state.withProperty(BlockSnowLayer.COVERED, true));
                return true;
            }
        } else if (offsetY == 0) {
            return false;
        }
        if (level.getBlockIdAt(offsetX + x, offsetY + y - 1, offsetZ + z, 0) == BlockID.GRASS) {
            section.setBlockState(x, y, z, state.withProperty(BlockSnowLayer.COVERED, true));
            return true;
        }
        
        return false;
    }
}
