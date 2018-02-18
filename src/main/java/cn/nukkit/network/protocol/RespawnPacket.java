package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;

/**
 * @author Nukkit Project Team
 */
public class RespawnPacket extends DataPacket {

    public float x;
    public float y;
    public float z;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("RESPAWN_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVector3f(this.x, this.y, this.z);
    }

}
