package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class Pufferfish extends Fish {

    public Pufferfish() {
        this(0, 1);
    }

    public Pufferfish(Integer meta) {
        this(meta, 1);
    }

    public Pufferfish(Integer meta, int count) {
        super(PUFFERFISH, meta, count, "Pufferfish");
    }

}
