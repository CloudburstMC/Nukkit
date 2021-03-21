package cn.nukkit.level.format.updater;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWall;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
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
        } catch (InvalidBlockStateException e) {
            // Block was on an invalid state, clearing the state but keeping the material type
            try {
                int data = state.getLegacyDamage() & 0xF;
                try {
                    BlockWall.WALL_BLOCK_TYPE.validateMeta(data, 0);
                } catch (InvalidBlockPropertyMetaException ignored) {
                    // Oh no! Somehow the wall type became invalid :/
                    // Unfortunately, our only option now is to convert it to a regular cobblestone wall
                    data = 0;
                }
                block = state.withData(data).getBlock(level, levelX, levelY, levelZ, 0);
            } catch (InvalidBlockStateException e2) {
                e.addSuppressed(e2);
                Server server = Server.getInstance();
                log.warn("Failed to update the block X:{}, Y:{}, Z:{} at {}, could not cast it to BlockWall. Section Version:{}, Updating To:{}, Server Version:{} {}", 
                        levelX, levelY, levelZ, level, section.getContentVersion(), ChunkUpdater.getCurrentContentVersion(), 
                        server.getNukkitVersion(), server.getGitCommit(), e);
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
