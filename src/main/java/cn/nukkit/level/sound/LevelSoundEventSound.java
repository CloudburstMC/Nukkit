package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author Tee7even
 */
public class LevelSoundEventSound extends Sound {
    protected final byte type;
    protected int extraData;
    protected int pitch;

    public LevelSoundEventSound(Vector3 pos, byte type) {
        this(pos, type, -1, -1);
    }

    public LevelSoundEventSound(Vector3 pos, byte type, int extraData) {
        this(pos, type, extraData, -1);
    }

    public LevelSoundEventSound(Vector3 pos, byte type, int extraData, int pitch) {
        super(pos.x, pos.y, pos.z);
        this.type = type;
        this.extraData = extraData;
        this.pitch = pitch;
    }

    @Override
    public DataPacket[] encode() {
        LevelSoundEventPacket pk = new LevelSoundEventPacket();
        pk.type = this.type;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.extraData = this.extraData;
        pk.pitch = this.pitch;

        return new DataPacket[]{pk};
    }
}
