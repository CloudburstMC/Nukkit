package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.sound in project Nukkit .
 */
public class GhastShootSound extends LevelEventSound {
    public GhastShootSound(Vector3 pos) {
        this(pos, 0);
    }

    public GhastShootSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_GHAST_SHOOT, pitch);
    }
}
