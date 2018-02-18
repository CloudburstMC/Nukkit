package cn.nukkit.network.protocol;

public class EntityPickRequestPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("ENTITY_PICK_REQUEST_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        //TODO
    }
}
