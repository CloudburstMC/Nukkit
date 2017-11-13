package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordStal extends ItemRecord {

    public ItemRecordStal() {
        this(0, 1);
    }

    public ItemRecordStal(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordStal(Integer meta, int count) {
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_STAL;
    }
}
