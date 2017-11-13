package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecord11 extends ItemRecord {

    public ItemRecord11() {
        this(0, 1);
    }

    public ItemRecord11(Integer meta) {
        this(meta, 1);
    }

    public ItemRecord11(Integer meta, int count) {
        super(RECORD_11, meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_11;
    }
}
