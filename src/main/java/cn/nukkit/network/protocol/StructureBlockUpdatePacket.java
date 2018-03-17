package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class StructureBlockUpdatePacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET;
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
