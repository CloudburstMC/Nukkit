package cn.nukkit.server.item;

import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordCat extends ItemRecord {

    public ItemRecordCat() {
        this(0, 1);
    }

    public ItemRecordCat(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordCat(Integer meta, int count) {
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_CAT;
    }
}
