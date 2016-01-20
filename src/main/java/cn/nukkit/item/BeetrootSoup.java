package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BeetrootSoup extends EdibleItem {

    public BeetrootSoup() {
        this(0, 1);
    }

    public BeetrootSoup(Integer meta) {
        this(meta, 1);
    }

    public BeetrootSoup(Integer meta, int count) {
        super(BEETROOT_SOUP, 0, count, "Beetroot Soup");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
