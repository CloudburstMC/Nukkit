package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordMall extends ItemRecord {

    public ItemRecordMall() {
        this(0, 1);
    }

    public ItemRecordMall(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordMall(Integer meta, int count) {
        super(RECORD_MALL, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.mall";
    }
}
