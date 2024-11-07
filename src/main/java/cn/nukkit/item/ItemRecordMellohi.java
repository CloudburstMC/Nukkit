package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordMellohi extends ItemRecord {

    public ItemRecordMellohi() {
        this(0, 1);
    }

    public ItemRecordMellohi(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordMellohi(Integer meta, int count) {
        super(RECORD_MELLOHI, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.mellohi";
    }

    @Override
    public String getDiscName() {
        return "C418 - mellohi";
    }
}
