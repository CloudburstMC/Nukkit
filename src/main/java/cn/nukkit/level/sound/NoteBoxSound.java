package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NoteBoxSound extends GenericSound {
    public NoteBoxSound(Vector3 pos) {
        this(pos, 0);
    }

    public NoteBoxSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_NOTEBOX, pitch);
    }
}
