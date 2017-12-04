package cn.nukkit.server.item;

import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordMall extends ItemRecord {

    public ItemRecordMall() {
        this(0, 1);
    }

    public ItemRecordMall(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordMall(Integer meta, int count) {
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_MALL;
    }
}
