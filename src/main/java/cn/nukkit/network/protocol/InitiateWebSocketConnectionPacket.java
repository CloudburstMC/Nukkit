package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class InitiateWebSocketConnectionPacket extends DataPacket {

	public String serverUri;

    @Override
    public byte pid() {
        return ProtocolInfo.INITIATE_WEB_SOCKET_CONNECTION_PACKET;
    }

    @Override
    public void decode() {
    	this.serverUri = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.serverUri);
    }
}
