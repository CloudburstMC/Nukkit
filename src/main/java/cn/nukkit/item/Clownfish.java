package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class Clownfish extends Fish {

    public Clownfish() {
        this(0, 1);
    }

    public Clownfish(Integer meta) {
        this(meta, 1);
    }

    public Clownfish(Integer meta, int count) {
        super(CLOWNFISH, meta, count, "Clownfish");
    }
}
