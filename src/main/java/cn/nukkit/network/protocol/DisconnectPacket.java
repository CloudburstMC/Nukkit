package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class DisconnectPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.DISCONNECT_PACKET;

    public boolean hideDisconnectionScreen;
    public String message;
    public String filteredMessage = "";

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.getVarInt(); // Disconnect fail reason
        this.hideDisconnectionScreen = this.getBoolean();
        this.message = this.getString();
        this.filteredMessage = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(0); // Disconnect fail reason UNKNOWN
        this.putBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            this.putString(this.message);
            this.putString(this.filteredMessage);
        }
    }
}
