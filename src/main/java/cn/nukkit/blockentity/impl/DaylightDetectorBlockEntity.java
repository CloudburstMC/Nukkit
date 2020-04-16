package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.DaylightDetector;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;

public class DaylightDetectorBlockEntity extends BaseBlockEntity implements DaylightDetector {


    public DaylightDetectorBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public boolean isValid() {
        Identifier blockId = getBlock().getId();
        return blockId == BlockIds.DAYLIGHT_DETECTOR || blockId == BlockIds.DAYLIGHT_DETECTOR_INVERTED;
    }

}
