package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;

public class SplashSound extends LevelEventSound {

    public SplashSound(Vector3 pos) {
        this(pos, 0);
    }

    public SplashSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_SPLASH, pitch);
    }
}
