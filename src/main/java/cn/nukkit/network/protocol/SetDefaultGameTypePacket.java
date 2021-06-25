package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetDefaultGameTypePacket extends DataPacket {

	public int gamemode;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET;
    }

    @Override
    public void decode() {
    	this.gamemode = this.getVerInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.gamemode);
    }
}
