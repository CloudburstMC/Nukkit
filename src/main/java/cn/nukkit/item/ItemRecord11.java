package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecord11 extends ItemRecord {

    public ItemRecord11() {
        this(0, 1);
    }

    public ItemRecord11(Integer meta) {
        this(meta, 1);
    }

    public ItemRecord11(Integer meta, int count) {
        super(RECORD_11, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.11";
    }
}
