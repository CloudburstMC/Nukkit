package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BeetrootSoup extends Item {

    public BeetrootSoup() {
        this(0, 1);
    }

    public BeetrootSoup(int meta) {
        this(meta, 1);
    }

    public BeetrootSoup(int meta, int count) {
        super(BEETROOT_SOUP, 0, count, "Beetroot Soup");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
