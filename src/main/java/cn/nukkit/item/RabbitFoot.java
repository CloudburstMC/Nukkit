package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class RabbitFoot extends Item {

    public RabbitFoot() {
        this(0, 1);
    }

    public RabbitFoot(Integer meta) {
        this(meta, 1);
    }

    public RabbitFoot(Integer meta, int count) {
        super(RABBIT_FOOT, meta, count, "Rabbit Foot");
    }
}
