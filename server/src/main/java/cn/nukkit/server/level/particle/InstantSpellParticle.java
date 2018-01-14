package cn.nukkit.server.level.particle;

import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.util.BlockColor;

/**
 * Created on 2016/1/4 by xtypr.
 * Package cn.nukkit.server.level.particle in project nukkit .
 */
public class InstantSpellParticle extends SpellParticle {
    protected int data;

    public InstantSpellParticle(Vector3 pos) {
        this(pos, 0);
    }

    public InstantSpellParticle(Vector3 pos, int data) {
        super(pos, data);
    }

    public InstantSpellParticle(Vector3 pos, BlockColor blockColor) {
        //alpha is ignored
        this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue());
    }

    public InstantSpellParticle(Vector3 pos, int r, int g, int b) {
        //this 0x01 is the only difference between instant spell and non-instant one
        super(pos, r, g, b, 0x01);
    }

}
