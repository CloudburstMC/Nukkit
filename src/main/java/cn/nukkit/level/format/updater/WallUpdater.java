package cn.nukkit.level.format.updater;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWall;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
class WallUpdater implements Updater {
    private final Level level;
    private final ChunkSection section;

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        if (state.getBlockId() != BlockID.COBBLE_WALL) {
            return false;
        }

        int levelX = offsetX + x;
        int levelY = offsetY + y;
        int levelZ = offsetZ + z;
        Block block;
        try {
            block = state.getBlock(level, levelX, levelY, levelZ, 0);
        } catch (InvalidBlockPropertyMetaException e) {
            // Block was on an invalid state, clearing the state but keeping the material type
            try {
                block = state.withData(state.getLegacyDamage() & 0xF).getBlock(level, levelX, levelY, levelZ, 0);
            } catch (InvalidBlockPropertyMetaException e2) {
                e.addSuppressed(e2);
                log.warn("Failed to update the block X:"+levelX+", Y:"+levelY+", Z:"+levelZ+" at "+level
                                +", could not cast it to BlockWall.", e);
                return false;
            }
        }
        
        BlockWall blockWall = (BlockWall) block;
        if (blockWall.autoConfigureState()) {
            section.setBlockStateAtLayer(x, y, z, 0, blockWall.getCurrentState());
            return true;
        }

        return false;
    }
}
