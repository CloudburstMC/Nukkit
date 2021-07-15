package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.CommandOriginData;
import lombok.ToString;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class CommandRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.COMMAND_REQUEST_PACKET;

    public String command;
    public CommandOriginData commandOriginData;
    public boolean isInternal;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.command = this.getString();
        this.commandOriginData = this.getCommandOriginData();
        this.isInternal = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.command);
        this.putCommandOriginData(this.commandOriginData);
        this.putBoolean(this.isInternal);
    }

    private CommandOriginData getCommandOriginData() {
        CommandOriginData commandOriginData = new CommandOriginData(CommandOriginData.Type.getById((int) this.getUnsignedVarInt()), this.getUUID(), this.getString());
        if (commandOriginData.type == CommandOriginData.Type.DEV_CONSOLE || commandOriginData.type == CommandOriginData.Type.TEST) {
            commandOriginData.entityUniqueId = this.getVarLong();
        }
        return commandOriginData;
    }

    private void putCommandOriginData(CommandOriginData commandOriginData) {
        this.putUnsignedVarInt(commandOriginData.type.ordinal());
        this.putUUID(commandOriginData.uuid);
        this.putString(commandOriginData.requestId);
        if (commandOriginData.type == CommandOriginData.Type.DEV_CONSOLE || commandOriginData.type == CommandOriginData.Type.TEST) {
            this.putVarLong(commandOriginData.entityUniqueId);
        }
    }
}
