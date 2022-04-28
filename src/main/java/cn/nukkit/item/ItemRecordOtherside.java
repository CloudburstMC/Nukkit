package cn.nukkit.item;

public class ItemRecordOtherside extends ItemRecord {

    public ItemRecordOtherside() {
        this(0, 1);
    }

    public ItemRecordOtherside(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordOtherside(Integer meta, int count) {
        super(RECORD_OTHERSIDE, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.otherside";
    }
}
