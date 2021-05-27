package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import lombok.RequiredArgsConstructor;

/**
 * @author joserobjr
 * @since 2021-05-25
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
class FrameUpdater implements Updater {
    private final ChunkSection section;

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        if (state.getBlockId() != BlockID.ITEM_FRAME_BLOCK) {
            return false;
        }

        return section.setBlockStateAtLayer(x, y, z, 0, state.withData(getNewData(state.getExactIntStorage())));
    }

    private int getNewData(int fromData) {
        switch (fromData) {
            case 0: // [199:0]
                return 5; //minecraft:frame;facing_direction=5;item_frame_map_bit=0
            case 1: // [199:1]
                return 4; //minecraft:frame;facing_direction=4;item_frame_map_bit=0
            case 2: // [199:2]
                return 3; //minecraft:frame;facing_direction=3;item_frame_map_bit=0
            case 3: // [199:3]
                return 2; //minecraft:frame;facing_direction=2;item_frame_map_bit=0
            case 4: // [199:4]
                return 8 + 5; //minecraft:frame;facing_direction=5;item_frame_map_bit=1
            case 5: // [199:5]
                return 8 + 4; //minecraft:frame;facing_direction=4;item_frame_map_bit=1
            case 6: // [199:6]
                return 8 + 3; //minecraft:frame;facing_direction=3;item_frame_map_bit=1
            case 7: // [199:7]
                return 8 + 2; //minecraft:frame;facing_direction=2;item_frame_map_bit=1
            default:
                return fromData;
        }
    }
}
