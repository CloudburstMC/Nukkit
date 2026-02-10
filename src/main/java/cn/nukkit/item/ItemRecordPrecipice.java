package cn.nukkit.item;

public class ItemRecordPrecipice extends ItemRecord {

    public ItemRecordPrecipice() {
        this(0, 1);
    }

    public ItemRecordPrecipice(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordPrecipice(Integer meta, int count) {
        super(RECORD_PRECIPICE, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.precipice";
    }

    @Override
    public String getDiscName() {
        return "Aaron Cherof - Precipice";
    }
}
