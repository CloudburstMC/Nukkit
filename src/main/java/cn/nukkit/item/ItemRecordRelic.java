package cn.nukkit.item;

public class ItemRecordRelic extends ItemRecord {

    public ItemRecordRelic() {
        this(0, 1);
    }

    public ItemRecordRelic(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordRelic(Integer meta, int count) {
        super(RECORD_RELIC, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.relic";
    }

    @Override
    public String getDiscName() {
        return "Aaron Cherof - Relic";
    }
}
