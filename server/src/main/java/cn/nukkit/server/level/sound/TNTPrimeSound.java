package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;

/**
 * Created by Pub4Game on 04.03.2016.
 */
public class TNTPrimeSound extends LevelEventSound {

    public TNTPrimeSound(Vector3 pos) {
        this(pos, 0);
    }

    public TNTPrimeSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_TNT, pitch);
    }
}
