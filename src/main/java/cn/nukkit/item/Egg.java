package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Egg extends Item {

    public Egg() {
        this(0, 1);
    }

    public Egg(Integer meta) {
        this(meta, 1);
    }

    public Egg(Integer meta, int count) {
        super(EGG, meta, count, "Egg");
    }
}
