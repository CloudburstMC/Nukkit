package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class MultiplayerSettingsPacket extends DataPacket {

	public static final int ACTION_ENABLE_MULTIPLAYER = 0;
	public static final int ACTION_DISABLE_MULTIPLAYER = 1;
	public static final int ACTION_REFRESH_JOIN_CODE = 2;
	
	public int action;

    @Override
    public byte pid() {
        return ProtocolInfo.MULTIPLAYER_SETTINGS_PACKET;
    }

    @Override
    public void decode() {
    	this.action = this.getVarInt();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putVarInt(this.action);
    }
}
