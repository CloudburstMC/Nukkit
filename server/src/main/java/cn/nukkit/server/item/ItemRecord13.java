package cn.nukkit.server.item;

/**
 * @author CreeperFace
 */
public class ItemRecord13 extends ItemRecord {

    public ItemRecord13() {
        this(0, 1);
    }

    public ItemRecord13(Integer meta) {
        this(meta, 1);
    }

    public ItemRecord13(Integer meta, int count) {
        super(meta, count);
    }

    @Override
    public int getSoundId() {
        return LevelSoundEventPacket.SOUND_RECORD_13;
    }
}
