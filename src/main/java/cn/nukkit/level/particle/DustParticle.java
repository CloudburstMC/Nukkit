package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class DustParticle extends GenericParticle {

    public DustParticle(final Vector3 pos, final BlockColor blockColor) {
        this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue(), blockColor.getAlpha());
    }

    public DustParticle(final Vector3 pos, final int r, final int g, final int b) {
        this(pos, r, g, b, 255);
    }

    public DustParticle(final Vector3 pos, final int r, final int g, final int b, final int a) {
        super(pos, Particle.TYPE_DUST, (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | b & 0xff);
    }

}
