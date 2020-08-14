package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import lombok.ToString;

@ToString
public class NPCRequestPacket extends DataPacket {

    @Since("1.4.0.0-PN")
    public long entityRuntimeId;

    @Since("1.4.0.0-PN")
    public RequestType requestType;

    @Since("1.4.0.0-PN")
    public String commandString;

    @Since("1.4.0.0-PN")
    public int actionType;

    @Since("1.4.0.0-PN")
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
        this.putByte((byte) requestType.ordinal());
        this.putString(this.commandString);
        this.putByte((byte) this.actionType);
    }

}
