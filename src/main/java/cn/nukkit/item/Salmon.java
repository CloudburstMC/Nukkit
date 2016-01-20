package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class Salmon extends Fish {

    public Salmon() {
        this(0, 1);
    }

    public Salmon(Integer meta) {
        this(meta, 1);
    }

    public Salmon(Integer meta, int count) {
        super(RAW_SALMON, meta, count, "Raw Salmon");
    }

}
