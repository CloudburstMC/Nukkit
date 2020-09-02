package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemRabbitCooked extends ItemEdible {

    public ItemRabbitCooked() {
        this(0, 1);
    }

    public ItemRabbitCooked(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitCooked(Integer meta, int count) {
        super(COOKED_RABBIT, meta, count, "Cooked Rabbit");
    }
}
