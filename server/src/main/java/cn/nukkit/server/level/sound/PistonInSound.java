package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class PistonInSound extends LevelSoundEventSound {

    public PistonInSound(Vector3 pos) {
        super(pos, LevelSoundEventPacket.SOUND_PISTON_IN, -1, 1);
    }
}
