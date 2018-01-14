package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ButtonClickSound extends LevelEventSound {
    public ButtonClickSound(Vector3 pos) {
        this(pos, 0);
    }

    public ButtonClickSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_BUTTON_CLICK, pitch);
    }
}
