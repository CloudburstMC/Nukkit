package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemGlowstoneDust extends Item {

    public ItemGlowstoneDust() {
        this(0, 1);
    }

    public ItemGlowstoneDust(Integer meta) {
        this(meta, 1);
    }

    public ItemGlowstoneDust(Integer meta, int count) {
        super(GLOWSTONE_DUST, meta, count, "Glowstone Dust");
    }
}
