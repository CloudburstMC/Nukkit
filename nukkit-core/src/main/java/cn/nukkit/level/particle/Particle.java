package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

import java.util.Collection;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public abstract class Particle extends Vector3 {

    public Particle(Vector3 pos){
        super(pos.getX(), pos.getY(), pos.getZ());
    }

    public abstract Collection<DataPacket> encode();
}
