package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SubClientLoginPacket extends DataPacket {

	public String connectionRequestData;

    @Override
    public byte pid() {
        return ProtocolInfo.SUB_CLIENT_LOGIN_PACKET;
    }

    @Override
    public void decode() {
    	this.connectionRequestData = this.getString();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putString(this.connectionRequestData);
    }
}
