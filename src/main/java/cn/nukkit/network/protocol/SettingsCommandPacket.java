package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SettingsCommandPacket extends DataPacket {

	public String command;
	public boolean suppressOutput;

    @Override
    public byte pid() {
        return ProtocolInfo.SETTINGS_COMMAND_PACKET;
    }

    @Override
    public void decode() {
    	this.command = this.getString();
		this.suppressOutput = this.getBoolean();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putString(this.command);
		this.putBoolean(this.suppressOutput);
    }
}
