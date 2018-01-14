package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.server.level.sound in project Nukkit .
 */
public class LaunchSound extends LevelEventSound {
    public LaunchSound(Vector3 pos) {
        this(pos, 0);
    }

    public LaunchSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_SHOOT, pitch);
    }
}
