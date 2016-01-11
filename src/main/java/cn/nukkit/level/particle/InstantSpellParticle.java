package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Color;

/**
 * Created on 2016/1/4 by xtypr.
 * Package cn.nukkit.level.particle in project nukkit .
 */
public class InstantSpellParticle extends SpellParticle {
    protected int data;

    public InstantSpellParticle(Vector3 pos) {
        this(pos, 0);
    }

    public InstantSpellParticle(Vector3 pos, int data) {
        super(pos, data);
    }

    public InstantSpellParticle(Vector3 pos, Color color) {
        //alpha is ignored
        this(pos, color.getRed(), color.getGreen(), color.getBlue());
    }

    public InstantSpellParticle(Vector3 pos, int r, int g, int b) {
        //this 0x01 is the only difference between instant spell and non-instant one
        super(pos, r, g, b, 0x01);
    }

}
