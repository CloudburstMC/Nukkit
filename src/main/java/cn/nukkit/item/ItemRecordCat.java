package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordCat extends ItemRecord {

    public ItemRecordCat() {
        this(0, 1);
    }

    public ItemRecordCat(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordCat(Integer meta, int count) {
        super(RECORD_CAT, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.cat";
    }

    @Override
    public String getDiscName() {
        return "C418 - cat";
    }
}
