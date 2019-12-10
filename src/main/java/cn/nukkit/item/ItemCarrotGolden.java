package cn.nukkit.item;

public class ItemCarrotGolden extends ItemEdible {
    public ItemCarrotGolden() {
        this(0, 1);
    }

    public ItemCarrotGolden(Integer meta) {
        this(meta, 1);
    }

    public ItemCarrotGolden(Integer meta, int count) {
        super(GOLDEN_CARROT, 0, count, "Golden Carrot");
    }
}
