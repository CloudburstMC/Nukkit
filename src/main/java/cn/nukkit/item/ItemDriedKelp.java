package cn.nukkit.item;

public class ItemDriedKelp extends ItemEdible {

    public ItemDriedKelp() {
        this(0, 1);
    }

    public ItemDriedKelp(Integer meta) {
        this(meta, 1);
    }

    public ItemDriedKelp(Integer meta, int count) {
        super(DRIED_KELP, 0, count, "Dried Kelp");
    }

    @Override
    public int getUseTicks() {
        return 15;
    }
}
