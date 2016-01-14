package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class CookedSalmon extends Fish {

    public CookedSalmon() {
        this(0, 1);
    }

    public CookedSalmon(Integer meta) {
        this(meta, 1);
    }

    public CookedSalmon(Integer meta, int count) {
        super(COOKED_SALMON, meta, count, "Cooked Salmon");
    }

}
