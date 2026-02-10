package cn.nukkit.item;

public class ItemRecordCreator extends ItemRecord {

    public ItemRecordCreator() {
        this(0, 1);
    }

    public ItemRecordCreator(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordCreator(Integer meta, int count) {
        super(RECORD_CREATOR, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.creator";
    }

    @Override
    public String getDiscName() {
        return "Lena Raine - Creator";
    }
}
