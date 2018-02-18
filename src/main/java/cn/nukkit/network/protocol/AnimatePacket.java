package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AnimatePacket extends DataPacket {

    public long eid;
    public int action;
    public float unknown;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("ANIMATE_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.action = this.getVarInt();
        this.eid = getEntityRuntimeId();
        if ((this.action & 0x80) != 0) {
            this.unknown = this.getLFloat();
        }
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset();
        this.putVarInt(this.action);
        this.putEntityRuntimeId(this.eid);
        if ((this.action & 0x80) != 0) {
            this.putLFloat(this.unknown);
        }
    }

}
