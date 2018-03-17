package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class SetHealthPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_HEALTH_PACKET;

    public int health;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.health);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
