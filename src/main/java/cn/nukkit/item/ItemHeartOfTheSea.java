package cn.nukkit.item;

public class ItemHeartOfTheSea extends Item {

    public ItemHeartOfTheSea() {
        this(0, 1);
    }

    public ItemHeartOfTheSea(Integer meta) {
        this(meta, 1);
    }

    public ItemHeartOfTheSea(Integer meta, int count) {
        super(HEART_OF_THE_SEA, meta, count, "Heart of the Sea");
    }
}
