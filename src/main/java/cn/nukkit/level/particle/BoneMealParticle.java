package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BoneMealParticle extends Particle {

    private Vector3 position;

    public BoneMealParticle(Vector3 pos) {
        super(pos.x, pos.y, pos.z);
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_PARTICLE_BONEMEAL;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = 0;

        return new BedrockPacket[]{pk};
    }
}
