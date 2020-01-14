package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3f;
import cn.nukkit.math.Vector3i;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BoneMealParticle extends Particle {

    private Vector3f position;

    public BoneMealParticle(Vector3i pos) {
        this(pos.add(0.5, 0.5, 0.5));
    }

    public BoneMealParticle(Vector3f pos) {
        super(pos.x, pos.y, pos.z);
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_PARTICLE_BONEMEAL;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = 0;

        return new DataPacket[]{pk};
    }
}
