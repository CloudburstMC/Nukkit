package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class ItemRabbitFoot extends Item {

    public ItemRabbitFoot() {
        this(0, 1);
    }

    public ItemRabbitFoot(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitFoot(Integer meta, int count) {
        super(RABBIT_FOOT, meta, count, "Rabbit Foot");
    }
}
