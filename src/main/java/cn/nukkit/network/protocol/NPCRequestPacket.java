package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NPCRequestPacket extends DataPacket {

    public int REQUEST_SET_ACTIONS = 0;
    public int REQUEST_EXECUTE_ACTION = 1;
    public int REQUEST_EXECUTE_CLOSING_COMMANDS = 2;
    public int REQUEST_SET_NAME = 3;
    public int REQUEST_SET_SKIN = 4;
    public int REQUEST_SET_INTERACTION_TEXT = 5;

    public int entityRuntimeId;

    public int requestType;
    
    public String commandString;

    public int actionType;
    
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
        this.putEntityRuntimeId(this.entityRuntimeId);
	    this.putByte(this.requestType);
	    this.putString(this.commandString);
	    this.putByte(this.actionType);
    }
    
}
