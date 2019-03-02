package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;

public class SpawnExperienceOrbPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET;

    public float x;
    public float y;
    public float z;
    public int amount;

    @Override
    public void decode() {
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.amount = (int) this.getUnsignedVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVector3f(this.x, this.y, this.z);
        this.putUnsignedVarInt(this.amount);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
