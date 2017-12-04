package cn.nukkit.server.network.protocol;

public class NPCRequestPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }
}
