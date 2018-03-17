package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class SetLastHurtByPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.SET_LAST_HURT_BY_PACKET;
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
