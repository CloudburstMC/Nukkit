package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecord13 extends ItemRecord {

    public ItemRecord13() {
        this(0, 1);
    }

    public ItemRecord13(Integer meta) {
        this(meta, 1);
    }

    public ItemRecord13(Integer meta, int count) {
        super(RECORD_13, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.13";
    }
}
