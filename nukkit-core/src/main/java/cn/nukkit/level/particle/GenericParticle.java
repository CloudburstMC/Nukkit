package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

import java.util.ArrayList;
import java.util.Collection;
/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class GenericParticle extends Particle {

    protected int id = 0;

    protected int data;

    public GenericParticle(Vector3 pos, int id){
        this(pos, id, 0);
    }

    public GenericParticle(Vector3 pos, int id, int data){
        super(pos);
        this.id = id;
        this.data = data;
    }

    @Override
    public Collection<DataPacket> encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.evid = 0x4000 | this.id;
        packet.x = this.getFloorX();
        packet.y = this.getFloorY();
        packet.z = this.getFloorZ();
        packet.data = this.data;
        packet.encode();

        Collection<DataPacket> pks = new ArrayList<>();
        pks.add(packet);
        return pks;
    }
}
