package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NPCRequestPacket extends DataPacket {

    public long entityRuntimeId;

    public int requestType;
    
    public String commandString;

    public int actionType;
    
    public enum Request {
    
        SET_ACTIONS(0),
        EXECUTE_ACTION(1),
        EXECUTE_CLOSING_COMMANDS(2),
        SET_NAME(3),
        SET_SKIN(4),
        SET_INTERACTION_TEXT(5);
    
        private final int value;
    
        Request(final int newValue) {
            value = newValue;
        }
    
        public int getValue() { 
            return value; 
        }
    
    }
    
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
        this.putByte((byte) this.requestType);
        this.putString(this.commandString);
        this.putByte((byte) this.actionType);
    }
    
}
