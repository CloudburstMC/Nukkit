package cn.nukkit.level.particle;

import cn.nukkit.utils.BlockColor;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created on 2016/1/4 by xtypr.
 * Package cn.nukkit.level.particle in project nukkit .
 */
public class InstantSpellParticle extends SpellParticle {
    protected int data;

    public InstantSpellParticle(Vector3f pos) {
        this(pos, 0);
    }

    public InstantSpellParticle(Vector3f pos, int data) {
        super(pos, data);
    }

    public InstantSpellParticle(Vector3f pos, BlockColor blockColor) {
        //alpha is ignored
        this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue());
    }

    public InstantSpellParticle(Vector3f pos, int r, int g, int b) {
        //this 0x01 is the only difference between instant spell and non-instant one
        super(pos, r, g, b, 0x01);
    }

}
