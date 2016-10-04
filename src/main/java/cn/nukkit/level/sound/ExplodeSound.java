package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

public class ExplodeSound extends GenericSound {

    public ExplodeSound(Vector3 pos) {
        this(pos, 0);
    }

    public ExplodeSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_EXPLODE, pitch);
    }
}
