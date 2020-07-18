package cn.nukkit.level.format.updater;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class OldWoodBarkUpdater implements Updater {
    private final ChunkSection section;
    private final int fromLog;
    private final int increment;

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        if (state.getBlockId() != fromLog) {
            return false;
        }

        @SuppressWarnings("deprecation") 
        int legacyDamage = state.getLegacyDamage();
        if ((legacyDamage & 0b1100) != 0b1100) {
            return false;
        }
        
        section.setBlockState(x, y, z, BlockState.of(BlockID.WOOD, (legacyDamage & 0b11) + increment));
        return true;
    }
}
