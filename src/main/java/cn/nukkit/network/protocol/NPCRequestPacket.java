package cn.nukkit.network.protocol;

import cn.nukkit.Player;

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

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
