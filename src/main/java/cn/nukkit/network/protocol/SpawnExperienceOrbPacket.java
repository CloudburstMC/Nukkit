package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

@ToString
public class SpawnExperienceOrbPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET;

    public Vector3f position;
    public int amount;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.position = this.getVector3f();
        this.amount = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVector3f(this.position);
        this.putVarInt(this.amount);
    }
}
