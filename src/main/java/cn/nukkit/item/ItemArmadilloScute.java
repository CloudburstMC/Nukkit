package cn.nukkit.item;

public class ItemArmadilloScute extends Item {

    public ItemArmadilloScute() {
        this(0, 1);
    }

    public ItemArmadilloScute(Integer meta) {
        this(meta, 1);
    }

    public ItemArmadilloScute(Integer meta, int count) {
        super(ARMADILLO_SCUTE, meta, count, "Armadillo Scute");
    }
}
