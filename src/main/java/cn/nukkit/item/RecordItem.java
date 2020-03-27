package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * @author CreeperFace
 */
public class RecordItem extends Item {
    private final String soundId;

    public RecordItem(Identifier id, String soundId) {
        super(id);
        this.soundId = soundId;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public static ItemFactory factory(String soundId) {
        return identifier -> new RecordItem(identifier, soundId);
    }

    public String getSoundId() {
        return soundId;
    }
}
