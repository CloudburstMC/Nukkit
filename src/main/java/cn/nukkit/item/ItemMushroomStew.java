package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemMushroomStew extends ItemEdible {

    public ItemMushroomStew() {
        this(0, 1);
    }

    public ItemMushroomStew(Integer meta) {
        this(meta, 1);
    }

    public ItemMushroomStew(Integer meta, int count) {
        super(MUSHROOM_STEW, 0, count, "Mushroom Stew");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
