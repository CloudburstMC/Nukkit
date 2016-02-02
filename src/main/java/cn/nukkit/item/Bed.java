package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bed extends Item {

    public Bed() {
        this(0, 1);
    }

    public Bed(Integer meta) {
        this(meta, 1);
    }

    public Bed(Integer meta, int count) {
        super(BED, 0, count, "Bed");
        this.block = new cn.nukkit.block.Bed();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
