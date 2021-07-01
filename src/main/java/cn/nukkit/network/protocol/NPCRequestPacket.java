package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NpcRequestPacket extends DataPacket {
	
	public static final byte REQUEST_SET_ACTIONS = 0;
	public static final byte REQUEST_EXECUTE_ACTION = 1;
	public static final byte REQUEST_EXECUTE_CLOSING_COMMANDS = 2;
	public static final byte REQUEST_SET_NAME = 3;
	public static final byte REQUEST_SET_SKIN = 4;
	public static final byte REQUEST_SET_INTERACTION_TEXT = 5;

	public long entityRuntimeId;
	public byte requestType;
	public String commandString;
	public byte actionType;

    @Override
    public byte pid() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.requestType = this.getByte();
        this.commandString = this.getString();
        this.actionType = this.getByte();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte(this.requestType);
        this.putString(this.commandString);
        this.putByte(this.actionType);
    }
}
