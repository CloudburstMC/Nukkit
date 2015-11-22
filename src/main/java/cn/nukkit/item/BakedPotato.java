package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BakedPotato extends Item {

    public BakedPotato() {
        this(0, 1);
    }

    public BakedPotato(Integer meta) {
        this(meta, 1);
    }

    public BakedPotato(Integer meta, int count) {
        super(BAKED_POTATO, meta, count, "Baked Potato");
    }

}
