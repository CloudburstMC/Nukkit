package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Snowball extends Item {

    public Snowball() {
        this(0, 1);
    }

    public Snowball(int meta) {
        this(meta, 1);
    }

    public Snowball(int meta, int count) {
        super(SNOWBALL, 0, count, "Snowball");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
