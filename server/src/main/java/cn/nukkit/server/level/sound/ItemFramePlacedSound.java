package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFramePlacedSound extends LevelEventSound {
    public ItemFramePlacedSound(Vector3 pos) {
        this(pos, 0);
    }

    public ItemFramePlacedSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_PLACED, pitch);
    }
}
