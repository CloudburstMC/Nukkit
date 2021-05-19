package cn.nukkit.item;
/**
 * @author Snake1999
 * @since 2016/1/14
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
