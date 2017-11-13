package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

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
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_FAR;
    }
}
