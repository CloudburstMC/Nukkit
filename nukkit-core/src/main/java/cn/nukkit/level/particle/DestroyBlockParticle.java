package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class DestroyBlockParticle extends Particle{

    protected int data;

    public DestroyBlockParticle(Vector3 pos, Block block) {
        super(pos);
        data = block.getId() + (block.getDamage() << 12);
    }

    @Override
    public Collection<DataPacket> encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.evid = LevelEventPacket.EVENT_PARTICLE_DESTROY;
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
