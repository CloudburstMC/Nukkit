package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created on 2016/1/4 by xtypr.
 * Package cn.nukkit.level.particle in project nukkit .
 */
public class InstantSpellParticle extends Particle {
    protected int data;

    public InstantSpellParticle(Vector3 pos) {
        this(pos, 0);
    }

    public InstantSpellParticle(Vector3 pos, int data) {
        super(pos.x, pos.y, pos.z);
        this.data = data;
    }

    public InstantSpellParticle(Vector3 pos, int r, int g, int b) {
        this(pos, r, g, b, 255);
    }

    public InstantSpellParticle(Vector3 pos, int r, int g, int b, int a) {
        this(pos, ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff));
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_PARTICLE_SPLASH; //todo change it according to packet catching
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = this.data;

        return new DataPacket[]{pk};
    }
}
