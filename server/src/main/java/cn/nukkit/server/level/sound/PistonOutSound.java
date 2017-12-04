package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class PistonOutSound extends LevelSoundEventSound {

    public PistonOutSound(Vector3 pos) {
        super(pos, LevelSoundEventPacket.SOUND_PISTON_OUT, -1, 1);
    }
}
