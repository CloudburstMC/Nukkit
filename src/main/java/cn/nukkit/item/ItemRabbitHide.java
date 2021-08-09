package cn.nukkit.item;

public class ItemRabbitHide extends Item {

    public ItemRabbitHide() {
        this(0, 1);
    }

    public ItemRabbitHide(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitHide(Integer meta, int count) {
        super(RABBIT_HIDE, 0, count, "Rabbit Hide");
    }
}
