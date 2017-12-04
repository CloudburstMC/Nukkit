package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.network.protocol.LevelEventPacket;

public class SplashSound extends LevelEventSound {

    public SplashSound(Vector3 pos) {
        this(pos, 0);
    }

    public SplashSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_SPLASH, pitch);
    }
}
