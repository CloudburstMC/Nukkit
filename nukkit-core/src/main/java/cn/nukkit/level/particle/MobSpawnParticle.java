package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class MobSpawnParticle extends Particle {

    protected int width;
    protected int height;

    public MobSpawnParticle(Vector3 pos, int width, int height) {
        super(pos);
        this.width = width;
        this.height = height;
    }

    @Override
    public List<DataPacket> encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.evid = LevelEventPacket.EVENT_PARTICLE_SPAWN;
        packet.x = this.getFloorX();
        packet.y = this.getFloorY();
        packet.z = this.getFloorZ();
        packet.data =  (this.width & 0xff) + ((this.height & 0xff) << 8);
        packet.encode();

        List<DataPacket> pks = new ArrayList<>();
        pks.add(packet);
        return pks;
    }
}
