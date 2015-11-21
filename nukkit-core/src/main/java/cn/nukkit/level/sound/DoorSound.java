package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.sound in project Nukkit .
 */
public class DoorSound extends GenericSound {
    public DoorSound(Vector3 pos){
        this(pos, 0);
    }

    public DoorSound(Vector3 pos, float pitch) {
        super(pos, 1003, pitch);
    }
}
