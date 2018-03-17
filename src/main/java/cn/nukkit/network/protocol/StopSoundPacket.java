package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class StopSoundPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.STOP_SOUND_PACKET;

    public String name;
    public boolean stopAll;

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
        this.putString(this.name);
        this.putBoolean(this.stopAll);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
