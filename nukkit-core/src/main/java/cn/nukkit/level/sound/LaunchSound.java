package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.sound in project Nukkit .
 */
public class LaunchSound extends GenericSound {
    public LaunchSound(Vector3 pos){
        this(pos, 0);
    }

    public LaunchSound(Vector3 pos, float pitch) {
        super(pos, 1002, pitch);
    }
}
