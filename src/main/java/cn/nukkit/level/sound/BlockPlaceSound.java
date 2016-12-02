package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created by CreeperFace on 13. 11. 2016.
 */
public class BlockPlaceSound extends GenericSound {

    public BlockPlaceSound(Vector3 pos) {
        this(pos, 0);
    }

    public BlockPlaceSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_BLOCK_PLACE, pitch);
    }
}