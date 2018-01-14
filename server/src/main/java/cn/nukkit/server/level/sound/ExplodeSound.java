package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;

public class ExplodeSound extends LevelEventSound {

    public ExplodeSound(Vector3 pos) {
        this(pos, 0);
    }

    public ExplodeSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_EXPLODE, pitch);
    }
}
