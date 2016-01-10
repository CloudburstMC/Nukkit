package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class DustParticle extends GenericParticle {

    public DustParticle(Vector3 pos, Color color) {
        this(pos, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public DustParticle(Vector3 pos, int r, int g, int b) {
        this(pos, r, g, b, 255);
    }

    public DustParticle(Vector3 pos, int r, int g, int b, int a) {
        super(pos, Particle.TYPE_DUST, ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff));
    }
}
