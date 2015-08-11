package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MushroomStew extends Item {

    public MushroomStew() {
        this(0, 1);
    }

    public MushroomStew(int meta) {
        this(meta, 1);
    }

    public MushroomStew(int meta, int count) {
        super(MUSHROOM_STEW, 0, count, "Mushroom Stew");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
