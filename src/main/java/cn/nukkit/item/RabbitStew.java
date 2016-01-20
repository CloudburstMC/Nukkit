package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class RabbitStew extends EdibleItem {

    public RabbitStew() {
        this(0, 1);
    }

    public RabbitStew(Integer meta) {
        this(meta, 1);
    }

    public RabbitStew(Integer meta, int count) {
        super(RABBIT_STEW, meta, count, "Rabbit Stew");
    }
}
