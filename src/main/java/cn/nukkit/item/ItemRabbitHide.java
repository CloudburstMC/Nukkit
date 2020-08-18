package cn.nukkit.item;

public class ItemRabbitHide extends Item {

    public ItemRabbitHide() {
        this(0, 1);
    }

    public ItemRabbitHide(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitHide(Integer meta, int count) {
        super(RABBIT_HIDE, meta, count, "Rabbit Hide");
    }
}
