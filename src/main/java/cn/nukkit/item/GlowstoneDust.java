package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GlowstoneDust extends Item {

    public GlowstoneDust() {
        this(0, 1);
    }

    public GlowstoneDust(Integer meta) {
        this(meta, 1);
    }

    public GlowstoneDust(Integer meta, int count) {
        super(GLOWSTONE_DUST, meta, count, "Glowstone Dust");
    }
}
