package cn.nukkit.item;

public class ItemShield extends ItemTool {

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

    @Override
    public int getMaxDurability() {
        return 337;
    }
}
