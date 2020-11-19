package cn.nukkit.item;

import cn.nukkit.api.Since;

/**
 * @author PetteriM1
 */
@Since("1.3.2.0-PN")
public class ItemRecordPigstep extends ItemRecord {

    @Since("1.3.2.0-PN")
    public ItemRecordPigstep() {
        this(0, 1);
    }

    @Since("1.3.2.0-PN")
    public ItemRecordPigstep(Integer meta) {
        this(meta, 1);
    }

    @Since("1.3.2.0-PN")
    public ItemRecordPigstep(Integer meta, int count) {
        super(RECORD_PIGSTEP, meta, count);
    }

    @Override
    public String getSoundId() {
        return "record.pigstep";
    }
}
