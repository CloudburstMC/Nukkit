package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class InitiateWebSocketConnectionPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.INITIATE_WEB_SOCKET_CONNECTION_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }

    @Override
    protected void handle(Player player) {

    }
}
