package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class RiderJumpPacket extends DataPacket {

    public int jumpStrength;

    @Override
    public byte pid() {
        return ProtocolInfo.RIDER_JUMP_PACKET;
    }

    @Override
    public void decode() {
        this.jumpStrength = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.jumpStrength);
    }
}
