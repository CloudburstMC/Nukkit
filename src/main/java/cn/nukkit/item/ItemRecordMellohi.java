package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

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
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_MELLOHI;
    }
}
