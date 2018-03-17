package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class SubClientLoginPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.SUB_CLIENT_LOGIN_PACKET;
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
        player.handle(this);
    }
}
