package cn.nukkit.server.item;

import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordWard extends ItemRecord {

    public ItemRecordWard() {
        this(0, 1);
    }

    public ItemRecordWard(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordWard(Integer meta, int count) {
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_WARD;
    }
}
