package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

import java.util.Collection;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.sound in project Nukkit .
 */
public abstract class Sound extends Vector3{

    public Sound(Vector3 pos){
        super(pos.getX(), pos.getY(), pos.getZ());
    }

    public abstract Collection<DataPacket> encode();
}
