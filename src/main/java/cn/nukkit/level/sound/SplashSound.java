package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

public class SplashSound extends GenericSound {

    public SplashSound(Vector3 pos) {
        this(pos, 0);
    }

    public SplashSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_SPLASH, pitch);
    }
}
