package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordWait extends ItemRecord {

    public ItemRecordWait() {
        this(0, 1);
    }

    public ItemRecordWait(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordWait(Integer meta, int count) {
        super(RECORD_WAIT, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.wait";
    }

    @Override
    public String getDiscName() {
        return "C418 - wait";
    }
}
