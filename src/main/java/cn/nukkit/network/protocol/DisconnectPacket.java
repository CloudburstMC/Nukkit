package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created by on 15-10-12.
 */
@ToString
public class DisconnectPacket extends DataPacket {

    public boolean hideDisconnectionScreen = false;
    public String message;

    @Override
    public byte pid() {
        return ProtocolInfo.DISCONNECT_PACKET;
    }

    @Override
    public void decode() {
        this.hideDisconnectionScreen = this.getBoolean();
        if (!this.hideDisconnectionScreen) {
            this.message = this.getString();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            this.putString(this.message);
        }
    }
}
