package cn.nukkit.server.item;

/**
 * @author CreeperFace
 */
public abstract class ItemRecord extends Item {

    public ItemRecord() {
        this(0, 1);
    }

    public ItemRecord(Integer meta) {
        this(meta, 1);
    }

    public ItemRecord(Integer meta, int count) {
        super(meta, count);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public abstract int getSoundId();
}
