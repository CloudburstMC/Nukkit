package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/27 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 * The name "spell" comes from minecraft wiki.
 */
public class SpellParticle extends Particle {

    protected final int data;

    public SpellParticle(final Vector3 pos) {
        this(pos, 0);
    }

    public SpellParticle(final Vector3 pos, final int data) {
        super(pos.x, pos.y, pos.z);
        this.data = data;
    }

    public SpellParticle(final Vector3 pos, final BlockColor blockColor) {
        //alpha is ignored
        this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue());
    }

    public SpellParticle(final Vector3 pos, final int r, final int g, final int b) {
        this(pos, r, g, b, 0x00);
    }

    protected SpellParticle(final Vector3 pos, final int r, final int g, final int b, final int a) {
        this(pos, (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | b & 0xff);
    }

    @Override
    public DataPacket[] encode() {
        final LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_PARTICLE_SPLASH;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = this.data;

        return new DataPacket[]{pk};
    }

}
