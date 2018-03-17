package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class SimpleEventPacket extends DataPacket {

    public short unknown;

    @Override
    public byte pid() {
        return ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putShort(this.unknown);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
