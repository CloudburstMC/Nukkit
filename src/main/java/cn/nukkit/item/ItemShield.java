package cn.nukkit.item;

public class ItemShield extends Item {

    public ItemShield() {
        this(0, 1);
    }

    public ItemShield(Integer meta) {
        this(meta, 1);
    }

    public ItemShield(Integer meta, int count) {
        super(SHIELD, meta, count, "Shield");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
