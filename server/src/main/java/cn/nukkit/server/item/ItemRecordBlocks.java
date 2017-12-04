package cn.nukkit.server.item;

import cn.nukkit.server.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordBlocks extends ItemRecord {

    public ItemRecordBlocks() {
        this(0, 1);
    }

    public ItemRecordBlocks(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordBlocks(Integer meta, int count) {
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_BLOCKS;
    }
}
