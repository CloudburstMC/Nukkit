package cn.nukkit.server.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.server.item in project nukkit.
 */
public class ItemSpiderEye extends Item {

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
