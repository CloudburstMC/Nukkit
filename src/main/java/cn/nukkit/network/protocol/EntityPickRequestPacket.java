package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EntityPickRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }
}
