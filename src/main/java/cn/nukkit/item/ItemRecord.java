package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public abstract class ItemRecord extends Item {

    public ItemRecord(int id, Integer meta, int count) {
        super(id, meta, count, "Music Disc");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public abstract int getSoundId();
}
