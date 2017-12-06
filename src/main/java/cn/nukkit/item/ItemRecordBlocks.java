package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public class ItemRecordBlocks extends ItemRecord {

    public ItemRecordBlocks() {
        this(0, 1);
    }

    public ItemRecordBlocks(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordBlocks(Integer meta, int count) {
        super(RECORD_BLOCKS, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.blocks";
    }
}
