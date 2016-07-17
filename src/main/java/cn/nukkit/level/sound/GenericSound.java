package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.sound in project Nukkit .
 */
public class GenericSound extends Sound {
    public GenericSound(Vector3 pos, int id) {
        this(pos, id, 0);
    }

    public GenericSound(Vector3 pos, int id, float pitch) {
        super(pos.x, pos.y, pos.z);
        this.id = id;
        this.pitch = pitch * 1000f;
    }

    protected float pitch = 0f;
    protected final int id;

    public float getPitch() {
        return this.pitch / 1000f;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch * 1000f;
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = this.id;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = (int) this.pitch;

        return new DataPacket[]{pk};
    }

}
