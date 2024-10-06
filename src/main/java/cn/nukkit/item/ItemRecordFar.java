package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordFar extends ItemRecord {

    public ItemRecordFar() {
        this(0, 1);
    }

    public ItemRecordFar(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordFar(Integer meta, int count) {
        super(RECORD_FAR, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.far";
    }

    @Override
    public String getDiscName() {
        return "C418 - far";
    }
}
