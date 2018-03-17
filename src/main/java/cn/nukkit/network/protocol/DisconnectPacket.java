package cn.nukkit.network.protocol;

import cn.nukkit.Player;

/**
 * Created by on 15-10-12.
 */
public class DisconnectPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.DISCONNECT_PACKET;

    public boolean hideDisconnectionScreen = false;
    public String message;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.hideDisconnectionScreen = this.getBoolean();
        this.message = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            this.putString(this.message);
        }
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
