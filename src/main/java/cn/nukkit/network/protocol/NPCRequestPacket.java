package cn.nukkit.network.protocol;

import lombok.ToString;
import java.util.Arrays;

@ToString
public class NPCRequestPacket extends DataPacket {

    public long entityRuntimeId;

    public RequestType requestType;

    public String commandString;

    public int actionType;

    public enum RequestType {

        SET_ACTIONS,
        EXECUTE_ACTION,
        EXECUTE_CLOSING_COMMANDS,
        SET_NAME,
        SET_SKIN,
        SET_INTERACTION_TEXT

    }

    @Override
    public byte pid() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.requestType = RequestType.values()[this.getByte()];
        this.commandString = this.getString();
        this.actionType = this.getByte();
    }

    @Override
    public void encode() {
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte((byte) Arrays.asList(RequestType.values()).indexOf(requestType));
        this.putString(this.commandString);
        this.putByte((byte) this.actionType);
    }

}
