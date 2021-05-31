package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.math.BlockFace;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BeehiveUpdater implements Updater {
    private final ChunkSection section;
    private boolean updateDirection;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BeehiveUpdater(ChunkSection section, boolean updateDirection) {
        this.section = section;
        this.updateDirection = updateDirection;
    }

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        int blockId = state.getBlockId();
        if (blockId == BlockID.BEEHIVE || blockId == BlockID.BEE_NEST) {
            @SuppressWarnings("deprecation") 
            int meta = state.getLegacyDamage();
            BlockFace face;
            int honeyLevel;
            if (updateDirection) {
                face = BlockFace.fromIndex(meta & 0b111);
                honeyLevel = (meta >> 3) & 0b111;
            } else {
                face = BlockFace.fromHorizontalIndex(meta & 0b11);
                honeyLevel = (meta >> 2) & 0b111;
            }
            
            if (honeyLevel > 5) {
                honeyLevel = 5;
            }
            
            int newMeta = (honeyLevel << 2) | face.getHorizontalIndex();
            if (newMeta != meta) {
                section.setBlockState(x, y, z, state.withData(newMeta));
                return true;
            }
        }
        return false;
    }

}
