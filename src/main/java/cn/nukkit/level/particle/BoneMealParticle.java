package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BoneMealParticle extends Particle {

    public BoneMealParticle(Vector3 pos) {
        super(pos.x, pos.y, pos.z);
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.evid = LevelEventPacket.EVENT_PARTICLE_BONEMEAL;
        packet.x = (float) this.x;
        packet.y = (float) this.y;
        packet.z = (float) this.z;
        packet.data = 0;
        packet.tryEncode();
        return new DataPacket[]{packet};
    }
}
