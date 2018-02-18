package cn.nukkit.network.protocol;

/**
 * Created by on 15-10-12.
 */
public class DisconnectPacket extends DataPacket {

    public boolean hideDisconnectionScreen = false;
    public String message;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("DISCONNECT_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.hideDisconnectionScreen = this.getBoolean();
        this.message = this.getString();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            this.putString(this.message);
        }
    }


}
