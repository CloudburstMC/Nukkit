package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Cake extends Item {

    public Cake() {
        this(0, 1);
    }

    public Cake(Integer meta) {
        this(meta, 1);
    }

    public Cake(Integer meta, int count) {
        super(CAKE, 0, count, "Cake");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
