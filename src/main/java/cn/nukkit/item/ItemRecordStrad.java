package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordStrad extends ItemRecord {

    public ItemRecordStrad() {
        this(0, 1);
    }

    public ItemRecordStrad(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordStrad(Integer meta, int count) {
        super(RECORD_STRAD, meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_STRAD;
    }
}
