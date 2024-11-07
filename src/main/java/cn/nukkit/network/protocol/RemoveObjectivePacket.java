package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class RemoveObjectivePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REMOVE_OBJECTIVE_PACKET;

    public String objectiveId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.objectiveId);
    }
}