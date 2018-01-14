package cn.nukkit.server.level.sound;

import cn.nukkit.server.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeverSound extends LevelEventSound {

    public LeverSound(Vector3 pos, boolean isPowerOn) {
        super(pos, LevelEventPacket.EVENT_SOUND_BUTTON_CLICK, isPowerOn ? 0.6f : 0.5f);
    }

}
