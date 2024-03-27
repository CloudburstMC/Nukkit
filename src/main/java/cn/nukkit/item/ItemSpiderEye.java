package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class ItemSpiderEye extends ItemEdible {

    public ItemSpiderEye() {
        this(0, 1);
    }

    public ItemSpiderEye(Integer meta) {
        this(meta, 1);
    }

    public ItemSpiderEye(Integer meta, int count) {
        super(SPIDER_EYE, meta, count, "Spider Eye");
    }
}
