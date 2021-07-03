package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BoneMealParticle extends Particle {

    private Vector3 position;

    public BoneMealParticle(Vector3 pos) {
        super(pos.x, pos.y, pos.z);
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.event = LevelEventPacket.EVENT_PARTICLE_BONEMEAL;
        pk.position = new Vector3f((float) this.x, (float) this.y, (float) this.z);

        return new DataPacket[]{pk};
    }
}
