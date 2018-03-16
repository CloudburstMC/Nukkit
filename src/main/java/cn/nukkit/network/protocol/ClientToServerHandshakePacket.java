package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class ClientToServerHandshakePacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET;
    }

    @Override
    public void decode() {
        //no content
    }

    @Override
    public void encode() {

    }

    @Override
    protected void handle(Player player) {

    }
}
