package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2016/1/4 by xtypr.
 * Package cn.nukkit.level.particle in project nukkit .
 */
public class InstantSpellParticle extends SpellParticle {

    protected int data;

    public InstantSpellParticle(final Vector3 pos) {
        this(pos, 0);
    }

    public InstantSpellParticle(final Vector3 pos, final int data) {
        super(pos, data);
    }

    public InstantSpellParticle(final Vector3 pos, final BlockColor blockColor) {
        //alpha is ignored
        this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue());
    }

    public InstantSpellParticle(final Vector3 pos, final int r, final int g, final int b) {
        //this 0x01 is the only difference between instant spell and non-instant one
        super(pos, r, g, b, 0x01);
    }

}
