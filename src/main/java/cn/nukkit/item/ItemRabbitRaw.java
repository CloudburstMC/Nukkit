package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemRabbitRaw extends ItemEdible {
    public ItemRabbitRaw() {
        this(0, 1);
    }

    public ItemRabbitRaw(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitRaw(Integer meta, int count) {
        super(RAW_RABBIT, meta, count, "Raw Rabbit");
    }

}
