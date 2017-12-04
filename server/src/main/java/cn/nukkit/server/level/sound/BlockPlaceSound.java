package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class BlockPlaceSound extends LevelSoundEventSound {
    public BlockPlaceSound(Vector3 pos, int blockId) {
        super(pos, LevelSoundEventPacket.SOUND_PLACE, blockId, 1);
    }
}