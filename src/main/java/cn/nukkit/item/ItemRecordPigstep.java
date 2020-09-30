package cn.nukkit.item;

/**
 * @author PetteriM1
 */
public class ItemRecordPigstep extends ItemRecord {

    public ItemRecordPigstep() {
        this(0, 1);
    }

    public ItemRecordPigstep(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordPigstep(Integer meta, int count) {
        super(RECORD_PIGSTEP, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.pigstep";
    }
}
