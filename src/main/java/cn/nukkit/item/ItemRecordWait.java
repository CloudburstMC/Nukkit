package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

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
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_WAIT;
    }
}
