package cn.nukkit.network.protocol;

public class SpawnExperienceOrbPacket extends DataPacket {

    public float x;
    public float y;
    public float z;
    public int amount;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SPAWN_EXPERIENCE_ORB_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVector3f(this.x, this.y, this.z);
        this.putUnsignedVarInt(this.amount);
    }

}
