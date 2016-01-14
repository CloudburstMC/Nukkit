package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class PoisonousPotato extends Potato {

    public PoisonousPotato() {
        this(0, 1);
    }

    public PoisonousPotato(Integer meta) {
        this(meta, 1);
    }

    public PoisonousPotato(Integer meta, int count) {
        super(POISONOUS_POTATO, meta, count, "Poisonous Potato");
    }
}
