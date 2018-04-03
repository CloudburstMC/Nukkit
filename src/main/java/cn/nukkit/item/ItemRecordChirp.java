package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordChirp extends ItemRecord {

    public ItemRecordChirp() {
        this(0, 1);
    }

    public ItemRecordChirp(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordChirp(Integer meta, int count) {
        super(RECORD_CHIRP, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.chirp";
    }
}
