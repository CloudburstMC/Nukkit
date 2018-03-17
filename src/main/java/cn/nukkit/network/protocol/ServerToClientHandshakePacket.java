package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class ServerToClientHandshakePacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.SERVER_TO_CLIENT_HANDSHAKE_PACKET;
    }

    public String publicKey;
    public String serverToken;
    public String privateKey;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
