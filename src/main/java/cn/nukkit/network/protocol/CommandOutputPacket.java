package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.CommandOriginData;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import lombok.ToString;

@ToString
public class CommandOutputPacket extends DataPacket {

    public static final byte TYPE_LAST = 1;
    public static final byte TYPE_SILENT = 2;
    public static final byte TYPE_ALL = 3;
    public static final byte TYPE_DATA_SET = 4;

    public CommandOriginData commandOriginData;
    public byte outputType;
    public int successCount;
    public CommandOutputMessage[] entries = new CommandOutputMessage[0];
    public String unknownString;

    @Override
    public byte pid() {
        return ProtocolInfo.COMMAND_OUTPUT_PACKET;
    }

    @Override
    public void decode() {
        this.commandOriginData = this.getCommandOriginData();
        this.outputType = this.getByte();
        this.successCount = (int) this.getUnsignedVarInt();
        int count = (int) this.getUnsignedVarInt();
        this.entries = new CommandOutputMessage[count];
        for (int i = 0; i < count; i++) {
            this.entries[i] = this.getCommandOutputMessage();
        }
        if (this.outputType == TYPE_DATA_SET) {
            this.unknownString = this.getString();
        }
    }

    @Override
    public void encode() {
        this.putCommandOriginData(this.commandOriginData);
        this.putByte(this.outputType);
        this.putUnsignedVarInt(this.successCount);
        this.putUnsignedVarInt(this.entries.length);
        for (CommandOutputMessage entry : this.entries) {
            this.putCommandOutputMessage(entry);
        }
        if (this.outputType == TYPE_DATA_SET) {
            this.putString(this.unknownString);
        }
    }

    private CommandOutputMessage getCommandOutputMessage() {
        CommandOutputMessage commandOutputMessage = new CommandOutputMessage();
        commandOutputMessage.isInternal = this.getBoolean();
        commandOutputMessage.messageId = this.getString();
        int count = (int) this.getUnsignedVarInt();
        commandOutputMessage.parameters = new String[count];
        for (int i = 0; i < count; i++) {
            commandOutputMessage.parameters[i] = this.getString();
        }
        return commandOutputMessage;
    }

    private void putCommandOutputMessage(CommandOutputMessage commandOutputMessage) {
        this.putBoolean(commandOutputMessage.isInternal);
        this.putString(commandOutputMessage.messageId);
        this.putUnsignedVarInt(commandOutputMessage.parameters.length);
        for (String parameter : commandOutputMessage.parameters) {
            this.putString(parameter);
        }
    }
}
