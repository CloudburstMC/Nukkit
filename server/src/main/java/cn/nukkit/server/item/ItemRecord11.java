package cn.nukkit.server.item;

import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

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
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_11;
    }
}
