package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;

/**
 * @author CreeperFace
 */
public class BlockPlaceSound extends LevelSoundEventSound {
    public BlockPlaceSound(Vector3 pos, int blockId) {
        super(pos, LevelSoundEventPacket.SOUND_PLACE, blockId, 1);
    }
}